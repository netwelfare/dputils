package com.netease.carinsu.service;

import com.netease.carinsu.dao.VehicleDao;
import com.netease.carinsu.dto.VehicleSearchDto;
import com.netease.carinsu.entity.Vehicle;
import com.netease.module.util.LoggerUtil;
import com.netease.module.util.MemCacheUtil;
import com.netease.util.FilePathUtil;
import com.netease.util.StarUtil;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.*;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * 更新索引按照update_time进行增量更新 只在lucene中存了web端车型查询需要用到的数据
 * 
 * @author lone
 * @Date 2015/9/28
 * @Time 10:28
 */
@Service
public class VehicleSearchService {

  public static final int BATCH_BUILD_INDEX_SIZE = 1000;
  public static final String CACHE_TIME_KEY = "VEHICLE_LAST_UPDATE_TIME_KEY";
  public static double BUFFER_SIZE = 256D;
  private static final String ID = "id";
  private static final String BRAND_NAME = "brandName";
  private static final String FAMILY_NAME = "familyName";
  private static final String VERSION_NAME = "versionName";
  private static final String FGW_CODE = "fgwCode";
  private static final String STANDARD_NAME = "standardName";
  private static final String PRICE = "price";
  private static final String ENGINEDESC = "engineDesc";
  private static final String GEARBOX_NAME = "gearboxName";
  private static final String KEYWORD = "keyword";
  private static final String SEAT_COUNT = "seatCount";
  private static final String VICEKEYWORD = "vicekeyword";
  private static final Version version = Version.LUCENE_33;

  private Directory directory;
  private Analyzer analyzer;
  private IndexSearcher indexSearcher;
  private ReadWriteLock readWriteLock;
  private Analyzer standardAnalyzer;

  @Autowired
  private VehicleDao vehicleDao;

  public VehicleSearchService() {

  }


  public Vehicle getVehicleById(String vehicleId) {
    if (vehicleId == null) {
      return null;
    }
    Map map = new HashMap();
    map.put("id", vehicleId);
    List<Vehicle> vehicles = vehicleDao.queryBy(map);
    return CollectionUtils.isEmpty(vehicles) ? null : vehicles.get(0);
  }

  @PostConstruct
  public void init() {
    String webroot = FilePathUtil.getAbsolutePathOfWebRoot();
    String path =
        webroot + File.separatorChar + ".." + File.separatorChar + "lucene" + File.separatorChar
            + "carinsu";
    File file = new File(path);
    if (!file.exists()) {
      file.mkdirs();
    }
    // 中文分词,按词组分词
    analyzer = new IKAnalyzer(true);
    standardAnalyzer = new StandardAnalyzer(version);
    readWriteLock = new ReentrantReadWriteLock();
    try {
      directory = FSDirectory.open(file);
      openIndexSearcher();
      //buildAllIndex(); 
    } catch (IOException e) {
      LoggerUtil.error("[init vehicle lucene error]:" + e.getMessage(), e);
    }
    
  }

  public VehicleSearchDto search(VehicleSearchVo searchVo) {
    VehicleSearchDto result = null;
    try {
      readWriteLock.readLock().lock();
      List<VehicleSearchVo> vehicles = new ArrayList<>();
      
      Query query = buildQueryExact(searchVo);
      TopDocs topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
      if(topDocs.totalHits==0){
        query = buildQuery(searchVo);
        topDocs = indexSearcher.search(query, Integer.MAX_VALUE);
      }
      
      if (topDocs != null) {
        ScoreDoc[] scoreDocs = indexSearcher.search(query, Integer.MAX_VALUE).scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
          Document document = indexSearcher.doc(scoreDoc.doc);
          //LoggerUtil.searchInfo("[查询到的document][keyword]:" + document.get(KEYWORD)+"   [vicekeyword]:"+ document.get(VICEKEYWORD)+"   [fgwCode]"+document.get(FGW_CODE));
          VehicleSearchVo vehicleVo = createVehicleVo(document);
          vehicles.add(vehicleVo);
        }
      }
      result =
          new VehicleSearchDto(vehicles, getKeywords("", searchVo.getKey()), vehicles.size(),
              searchVo.getPage(), searchVo.pageSize);

    } catch (ParseException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (result == null) {
        LoggerUtil.debugInfo("[search vehicle] condition is :" + searchVo + "error happened");
      } else {
        LoggerUtil.debugInfo("[search vehicle] condition is :" + searchVo + ", hit "
            + result.getPagination().getTotalItems() + "items");
      }
      readWriteLock.readLock().unlock();
    }
    return result;
  }

  private List<String> getKeywords(String column, String keyword) throws IOException {
    List<String> keywords = new ArrayList<>();
    TokenStream ts = analyzer.tokenStream(column, new StringReader(keyword));
    ts.reset();
    CharTermAttribute termAtt = ts.getAttribute(CharTermAttribute.class);
    while (ts.incrementToken()) {
      keywords.add(termAtt.toString());
    }
    if (keywords.isEmpty()) {
      keywords.add(keyword);
    }
    //LoggerUtil.searchInfo("[分词list]：" + keywords);

    return keywords;
  }

  
  private Query buildQuery(VehicleSearchVo searchVo) throws ParseException,
      IOException {
    BooleanQuery rootQuery = new BooleanQuery();
    /*if (strategy) {
      rootQuery.add(createParseQuery(KEYWORD, searchVo.getKey()), BooleanClause.Occur.MUST);
    } else {
      rootQuery.add(createParseQuery(KEYWORD, searchVo.getKey()), BooleanClause.Occur.SHOULD);
    }*/
    rootQuery.add(createParseQuery(KEYWORD, searchVo.getKey()), BooleanClause.Occur.SHOULD);
    rootQuery.add(createParseQuery(VICEKEYWORD, searchVo.getKey()), BooleanClause.Occur.SHOULD);
    if (StringUtils.isNotBlank(searchVo.getFamilyName())) {
      rootQuery.add(new PrefixQuery(new Term(FAMILY_NAME, searchVo.getFamilyName())),
          BooleanClause.Occur.MUST);
    }
    if (StringUtils.isNotBlank(searchVo.getBrandName())) {
      rootQuery.add(new PrefixQuery(new Term(BRAND_NAME, searchVo.getBrandName())),
          BooleanClause.Occur.MUST);
    }
    if (StringUtils.isNotBlank(searchVo.getEngineDesc())) {
      rootQuery.add(new PrefixQuery(new Term(ENGINEDESC, searchVo.getEngineDesc())),
          BooleanClause.Occur.MUST);
    }
    if (StringUtils.isNotBlank(searchVo.getGearboxName())) {
      rootQuery.add(new PrefixQuery(new Term(GEARBOX_NAME, searchVo.getGearboxName())),
          BooleanClause.Occur.MUST);
    }
    return rootQuery;
  }
  private Query buildQueryExact(VehicleSearchVo searchVo) throws ParseException, IOException{
    VehicleSearchVo searchVoExact = searchVo.clone();
    String key = searchVo.getKey();
    searchVoExact.setKey(this.getExactKey(key));
    return this.buildQuery(searchVoExact);
  }
  private String getExactKey(String key){
    String result="";
    boolean sign = false;
    for (int i = 0; i < key.length()-1; i++) {
      String behand = String.valueOf(key.charAt(i));
      String after = String.valueOf(key.charAt(i+1));
      if ((!behand.matches("^[\u4E00-\u9FFF]+$")&&!after.matches("^[\u4E00-\u9FFF]+$")) || (!behand.matches("^[\u4E00-\u9FFF]+$")&&sign)) {
        result += behand;
        sign = true;
      }else{
        sign = false;
      }
    }
    return result;
  }
  private Query createParseQuery(String key, String value) throws ParseException, IOException {
    List<String> list = getKeywords(key, value);
    if (list == null || list.size() == 0) {
      return null;
    }
    BooleanQuery query = new BooleanQuery();
    query.add(new TermQuery(new Term(key, list.get(0))), BooleanClause.Occur.SHOULD);
    if (list.size() > 1) {
      for (int i = 1; i < list.size(); i++) {
        query.add(new TermQuery(new Term(key, list.get(i))), BooleanClause.Occur.SHOULD);
      }
    }
    return query;
  }

  private VehicleSearchVo createVehicleVo(Document document) {
    VehicleSearchVo vehicle = new VehicleSearchVo();
    vehicle.setId(document.get(ID));
    vehicle.setBrandName(document.get(BRAND_NAME));
    vehicle.setFamilyName(document.get(FAMILY_NAME));
    vehicle.setVersionName(document.get(VERSION_NAME));
    vehicle.setStandardName(addMask(document.get(STANDARD_NAME),document.get(FGW_CODE))); //车型号添加*遮掩
    vehicle.setPrice(Integer.valueOf(document.get(PRICE)));
    vehicle.setEngineDesc(document.get(ENGINEDESC));
    vehicle.setGearboxName(document.get(GEARBOX_NAME));
    vehicle.setSeatCount(document.get(SEAT_COUNT));
    return vehicle;

  }

  private String addMask(String standardName, String fgwCode) {
    LoggerUtil.searchInfo("fgwCode:" + fgwCode+"      standardName:"+standardName);
    
    String str0 = "";
    //特殊处理数据表中 standarName不包含fgwCode的情况
    if (fgwCode==null || !standardName.contains(fgwCode)) {
      LoggerUtil.searchInfo("fgwCode为null,standarName:"+standardName);
      boolean sign = false;
      for (int i = 0; i < standardName.length()-1; i++) {
        String behand = String.valueOf(standardName.charAt(i));
        String after = String.valueOf(standardName.charAt(i+1));
        if ((!behand.matches("^[\u4E00-\u9FFF]+$")&&!after.matches("^[\u4E00-\u9FFF]+$")) || (!behand.matches("^[\u4E00-\u9FFF]+$")&&sign)) {
          str0 += behand;
          sign = true;
        }
      }
      fgwCode = str0;
    }
    String tempStr = StarUtil.addStar(fgwCode);
    LoggerUtil.searchInfo("[fgwCode]:"+fgwCode+"       [temp]:"+tempStr);
    String result = standardName.replace(fgwCode, tempStr);
    LoggerUtil.searchInfo("[result]:"+result);
    return result;
  }
   
  

  public void buildAllIndex() {
    buildIndexByTime(0);
    LoggerUtil.searchInfo("索引更新完成");
  }

  public void buildIncrease() {
    Object obj = MemCacheUtil.get(CACHE_TIME_KEY);
    long lastUpdateMills = obj == null ? 0 : (Long) obj;
    buildIndexByTime(lastUpdateMills);
  }

  public void buildIndexByTime(long startTimeMills) {
    Date date = new Date(startTimeMills);
    Map map = new HashMap<>();
    //map.put("updateTimeLower", date);
    //map.put("orderByUpdateTimeAsc", true);
    List<Vehicle> vehicles = vehicleDao.queryAll();
    if (CollectionUtils.isEmpty(vehicles)) {
      return;
    }
    int start = 0;
    while (start + BATCH_BUILD_INDEX_SIZE < vehicles.size()) {
      int end = start + BATCH_BUILD_INDEX_SIZE;
      List<Vehicle> subVehicles = vehicles.subList(start, end);
      batchBuildIndex(subVehicles);
      start = end;
    }
    batchBuildIndex(vehicles.subList(start, vehicles.size()));
    long lastUpdateTimeMills = vehicles.get(vehicles.size() - 1).getUpdateTime().getTime();
    MemCacheUtil.set(CACHE_TIME_KEY, lastUpdateTimeMills, 0);
  }


  public void batchBuildIndex(List<Vehicle> vehicles) {
    IndexWriter indexWriter = null;
    try {
      indexWriter = getIndexWrite(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
      for (Vehicle vehicle : vehicles) {
        Document document = getDocument(vehicle);
        indexWriter.updateDocument(new Term("id", vehicle.getId()), document, analyzer);
      }
      openIndexSearcher();
    } catch (IOException e) {
      LoggerUtil.error(
          "[vehicle search error]error happens when update lucene index:" + e.getMessage(), e);
    } finally {
      if (indexWriter == null) {
        return;
      }
      try {
        indexWriter.optimize(); // 优化索引
        indexWriter.close();
      } catch (IOException e) {
        LoggerUtil.error(
            "[vehicle search error]error happens when close index writer:" + e.getMessage(), e);
      }
    }
  }

  /**
   * 增加或新建索引以后,重新打开索引服务
   */
  private void openIndexSearcher() {
    try {
      if (indexSearcher == null) {
        indexSearcher = new IndexSearcher(directory);
      } else {
        try {
          readWriteLock.writeLock().lock();
          IndexReader indexReader = indexSearcher.getIndexReader();
          if (!indexReader.isCurrent()) {
            indexReader = IndexReader.open(directory);
            indexSearcher = new IndexSearcher(indexReader);
          }
        } finally {
          readWriteLock.writeLock().unlock();
        }
      }
    } catch (IOException e) {
      LoggerUtil.error(
          "[vehicle earch error]error happens when openIndexSearcher:" + e.getMessage(), e);
    }
  }

  private Document getDocument(Vehicle vehicle) {
    Document document = new Document();
    document.add(new Field(ID, vehicle.getId(), Field.Store.YES, Field.Index.NOT_ANALYZED));
    Field brandNameField =
        new Field(BRAND_NAME, vehicle.getBrandName(), Field.Store.YES, Field.Index.NOT_ANALYZED);
    //brandNameField.setBoost(2F);
    document.add(brandNameField);
    document.add(new Field(FAMILY_NAME, vehicle.getFamilyName(), Field.Store.YES,
        Field.Index.NOT_ANALYZED));
    document.add(new Field(VERSION_NAME, vehicle.getVersionName(), Field.Store.YES,
        Field.Index.NOT_ANALYZED));
    document.add(new Field(STANDARD_NAME, vehicle.getStandardName(), Field.Store.YES,
        Field.Index.NOT_ANALYZED));
    document.add(new Field(PRICE, String.valueOf(vehicle.getPrice()), Field.Store.YES,
        Field.Index.NO));
    document.add(new Field(ENGINEDESC, vehicle.getEngineDesc(), Field.Store.YES,
        Field.Index.NOT_ANALYZED));
    document.add(new Field(GEARBOX_NAME, vehicle.getGearBoxStringName(), Field.Store.YES,
        Field.Index.NOT_ANALYZED));
    String seatCount = vehicle.getSeatCount() == null ? "" : String.valueOf(vehicle.getSeatCount());
    document.add(new Field(SEAT_COUNT, seatCount, Field.Store.YES, Field.Index.NOT_ANALYZED));
    String fgwCode = vehicle.getFgwCode() == null ? "" : vehicle.getFgwCode();
    document.add(new Field(FGW_CODE, fgwCode,Field.Store.YES,Field.Index.NOT_ANALYZED));
    String brandAndFamily =
        vehicle.getBrandName() + vehicle.getFamilyName()  + stringSeparate(vehicle.getBrandName())
        + stringSeparate(vehicle.getFamilyName());
    Field keyField = new Field(KEYWORD, brandAndFamily, Field.Store.YES, Field.Index.ANALYZED);
    keyField.setBoost(2F);
    document.add(keyField);
    String forSearch = vehicle.getStandardName()+vehicle.getVersionName();
    document.add(new Field(VICEKEYWORD, forSearch, Field.Store.YES, Field.Index.ANALYZED));

    return document;
  }

  private String stringSeparate(String string) {
    char[] strChar = string.toCharArray();
    StringBuilder sb = new StringBuilder();
    for (char charTemp : strChar) {
      sb.append("|" + charTemp);
    }
    return sb.toString();
  }

  private IndexWriter getIndexWrite(IndexWriterConfig.OpenMode openMode) throws IOException {
    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_33, analyzer);
    config.setOpenMode(openMode);
    config.setRAMBufferSizeMB(BUFFER_SIZE);
    IndexWriter indexWriter = new IndexWriter(directory, config);
    return indexWriter;
  }


  public static class VehicleSearchVo implements Cloneable {
    private String key;
    private String id;
    private Integer page = 1;
    private Integer pageSize = 5;
    private String brandName; // 品牌
    
    private String standardName; // 型号
    private String familyName; // 系列
    private String engineDesc; // 排量
    private String gearboxName; // 档位
    private String versionName; // 版本
    private String seatCount; // 座椅数
    private Integer price; // 价格

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public String getKey() {
      return key;
    }

    public void setKey(String key) {
      this.key = key;
    }

    public Integer getPage() {
      return page;
    }

    public void setPage(Integer page) {
      this.page = page;
    }

    public Integer getPageSize() {
      return pageSize;
    }

    public void setPageSize(Integer pageSize) {
      this.pageSize = pageSize;
    }

    public String getBrandName() {
      return brandName;
    }

    public void setBrandName(String brandName) {
      this.brandName = brandName;
    }

    public String getFamilyName() {
      return familyName;
    }

    public void setFamilyName(String familyName) {
      this.familyName = familyName;
    }

    public String getEngineDesc() {
      return engineDesc;
    }

    public void setEngineDesc(String engineDesc) {
      this.engineDesc = engineDesc;
    }

    public String getGearboxName() {
      return gearboxName;
    }

    public void setGearboxName(String gearboxName) {
      this.gearboxName = gearboxName;
    }

    public String getSeatCount() {
      return seatCount;
    }

    public void setSeatCount(String seatCount) {
      this.seatCount = seatCount;
    }


    public String getStandardName() {
      return standardName;
    }

    public void setStandardName(String standardName) {
      this.standardName = standardName;
    }

    public String getVersionName() {
      return versionName;
    }

    public void setVersionName(String versionName) {
      this.versionName = versionName;
    }

    public Integer getPrice() {
      return price;
    }

    public void setPrice(Integer price) {
      this.price = price;
    }

    public VehicleSearchVo clone() {
      VehicleSearchVo o = null;
      try {
        o = (VehicleSearchVo) super.clone();
      } catch (CloneNotSupportedException e) {
        LoggerUtil.error("VehicleSearchVo clone error", e);
      }
      return o;
    }   
    
    @Override
    public String toString() {
      return "VehicleSearchVo{" + "key='" + key + '\'' + ", id='" + id + '\'' + ", page=" + page
          + ", pageSize=" + pageSize + ", brandName='" + brandName + '\'' + ", standardName='"
          + standardName + '\'' + ", familyName='" + familyName + '\'' + ", engineDesc='"
          + engineDesc + '\'' + ", gearboxName='" + gearboxName + '\'' + ", versionName='"
          + versionName + '\'' + ", seatCount='" + seatCount + '\'' + ", price=" + price + '}';
    }
  }
}
