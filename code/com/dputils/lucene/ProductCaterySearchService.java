package com.netease.insurance.service.product;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.dputils.lang.StringUtils;
import com.dputils.log.Log;
import com.dputils.log.LogFactory;

@Service
@EventBusService
public class ProductCaterySearchService
{
	private static Log log = LogFactory.getLog(ProductCaterySearchService.class);
	private static IndexSearcher searcher;
	private static IndexSearcher categorySearcher;
	private static Sort defaultSort = new Sort(new SortField("priority", SortField.INT, true),
			new SortField("salesCount", SortField.INT, true));
	private static Sort salesSort = new Sort(new SortField("salesCount", SortField.INT, true));
	private static Sort prioritySort = new Sort(new SortField("priority", SortField.INT, true));

	@Autowired
	private SearchTypeService searchTypeService;
	@Autowired
	private ProductService productService;

	@Autowired
	private ShopGoodsService shopGoodsService;

	public static Analyzer getAnalyzer()
	{

		// PerFieldAnalyzerWrapper analyzer = new PerFieldAnalyzerWrapper(new IKAnalyzer());
		// analyzer.addAnalyzer("keywords", new
		// WhitespaceAnalyzer(Version.LUCENE_33));
		// analyzer.addAnalyzer("keywords", new WeighingAnalyzer());
		// return analyzer;
		return new WhitespaceAnalyzer(Version.LUCENE_33);
	}

	public Sort getSort(String sortby, boolean reverse)
	{
		if ("salesCount".equals(sortby) || "sales".equals(sortby))
			return salesSort;
		if ("price".equals(sortby))
			return new Sort(new SortField("price", SortField.FLOAT, reverse));
		return defaultSort;
	}

	@Subscribe(isAsyn = true)
	public void onModuleRefresh(RefreshModuleEvent event)
	{
		if (event.getModuleName().equals(RefreshConstant.REFRESH_MODULE_GOODS))
		{
			productService.onModuleRefresh(event);
			doIndex();
		}

	}

	public void doIndex()
	{
		log.info("build product search index start");
		try
		{
			Directory idx = new RAMDirectory();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_33, getAnalyzer());
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(idx, iwc);

			Map<String, Map<String, Object>> category = new HashMap<String, Map<String, Object>>(50);
			for (SearchTypeGoodsDto dto : searchTypeService.getGoodsTypes().values())
			{
				Map goods = productService.queryAllGoodsMap().get(dto.getGoodsId().toString());
				if (goods == null)
				{
					log.error("cant find goods in search data, goodsId " + dto.getGoodsId().toString());
					continue;
				}
				/**
				 * 已发布、下架产品不在大厅显示
				 */
				if (goods.get("STATUS") != null)
				{
					String strId = goods.get("STATUS").toString();
					if (StringUtils.isNotBlank(strId))
					{
						Integer gId = Integer.parseInt(strId);
						if (gId.equals(Constant.RELEASED) || gId.equals(Constant.AWAYSALE))
						{
							continue;
						}
					}
				}
				String goodsId = dto.getGoodsId().toString();
				String merchantId = (goods.get("INSUMERCHANTID") != null ? goods.get("INSUMERCHANTID")
						: goods.get("MERCHANTID")).toString();
				String merchantName = MerchantConstant.getMerchantNameById(merchantId);
				if (StringUtils.isNotBlank(merchantName))// 统计各保险公司产品数量
				{
					Map<String, Object> entry = category.get(merchantName);
					if (entry == null)
					{
						entry = new HashMap<String, Object>();
						entry.put("name", merchantName);
						entry.put("id", new HashSet());
						entry.put("goodsIds", new HashSet());
						entry.put("merchantIds", new HashSet());
						entry.put("priority", 100000);
						entry.put("type", "1");
						category.put(merchantName, entry);
					}
					((Set) entry.get("goodsIds")).add(goodsId);
					((Set) entry.get("merchantIds")).add(merchantId);
				}
				if (dto.getCategory() != null && dto.getCategory().size() > 0)// 统计各分类产品数量
				{
					for (String categoryId : dto.getCategory())
					{
						SearchType searchType = searchTypeService.getSearchType(categoryId);
						Map<String, Object> entry = category.get(searchType.getNameCn());
						if (entry == null)
						{
							entry = new HashMap<String, Object>();
							entry.put("name", searchType.getNameCn());
							entry.put("id", new HashSet());
							entry.put("goodsIds", new HashSet());
							entry.put("merchantIds", new HashSet());
							entry.put("priority", searchType.getPriority() * 10);
							entry.put("type", "2");
							category.put(searchType.getNameCn(), entry);
						}
						((Set) entry.get("id")).add(categoryId);
						((Set) entry.get("goodsIds")).add(goodsId);
						((Set) entry.get("merchantIds")).add(merchantId);
					}
				}
				if (dto.getGroups() != null && dto.getGroups().size() > 0)// 统计各分类下搜索条件产品数量
				{
					for (List<String> list : dto.getGroups().values())
					{
						for (String groupItem : list)
						{
							SearchType searchType = searchTypeService.getSearchType(groupItem);
							Map<String, Object> entry = category.get(searchType.getNameCn());
							if (entry == null)
							{
								entry = new HashMap<String, Object>();
								entry.put("name", searchType.getNameCn());
								entry.put("id", new HashSet());
								entry.put("goodsIds", new HashSet());
								entry.put("merchantIds", new HashSet());
								entry.put("priority", searchType.getPriority() * 1);
								entry.put("type", "3");
								category.put(searchType.getNameCn(), entry);
							}
							((Set) entry.get("id")).add(groupItem);
							((Set) entry.get("goodsIds")).add(goodsId);
							((Set) entry.get("merchantIds")).add(merchantId);
						}
					}
				}
				Document doc = new Document();
				doc.add(new Field("goodsId", dto.getGoodsId().toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("name", goods.get("NAME").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				if (dto.getCategory() == null || dto.getCategory().isEmpty())
				{
					log.error("can't find category name, goods_id " + dto.getGoodsId());
					continue;
				}
				doc.add(new Field("category", StringUtils.join(dto.getCategory(), ' '), Field.Store.YES,
						Field.Index.ANALYZED));
				if (dto.getGroups() != null)
				{
					Set<String> typeIds = new HashSet<String>();
					for (Entry<String, List<String>> entry : dto.getGroups().entrySet())
					{
						typeIds.addAll(entry.getValue());
						doc.add(new Field(entry.getKey(), StringUtils.join(entry.getValue(), ' '), Field.Store.YES,
								Field.Index.ANALYZED));
					}
					doc.add(new Field("typeIds", StringUtils.join(typeIds, ' '), Field.Store.YES,
							Field.Index.ANALYZED));
				}
				doc.add(new Field("merchantId", merchantId, Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("salesCount", goods.get("SALESCOUNT").toString(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("price", goods.get("PRICE").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("priority", goods.get("PRIORITY").toString(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("keywords", goods.get("KEYWORDS").toString(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				writer.addDocument(doc);
			}
			log.info("build product search index successful");
			writer.close();
			searcher = new IndexSearcher(idx);// 初始化searcher
			// searcher.setSimilarity(new WeighingSimilarity());
			// defaultQuery = new TermQuery(new Term("keywords", keywordsService.getRealWord("kw000")));
			doCategoryIndex(category);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			log.error("not found search data file, can not use search functions, please check! " + e.getMessage());
		}
		catch (Exception e)
		{
			log.error("error occurs while doindex " + e.getMessage());
			log.error(e.getMessage(), e);
		}
	}

	protected void doCategoryIndex(Map<String, Map<String, Object>> category)
	{
		log.info("build product search category index start");
		try
		{
			Directory idx = new RAMDirectory();
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_33, getAnalyzer());
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(idx, iwc);

			for (Map<String, Object> entry : category.values())
			{
				Document doc = new Document();
				doc.add(new Field("name", entry.get("name").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("typeIds", StringUtils.join((Set) entry.get("id"), ' '), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("merchantIds", StringUtils.join((Set) entry.get("merchantIds"), ' '), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("count", String.valueOf(((Set) entry.get("goodsIds")).size()), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				doc.add(new Field("type", entry.get("type").toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
				doc.add(new Field("priority", entry.get("priority").toString(), Field.Store.YES,
						Field.Index.NOT_ANALYZED));
				writer.addDocument(doc);
			}
			log.info("build product search category index successful");
			writer.close();
			categorySearcher = new IndexSearcher(idx);// 初始化searcher
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			log.error("not found search data file, can not use search functions, please check! " + e.getMessage());
		}
		catch (Exception e)
		{
			log.error("error occurs while doindex " + e.getMessage());
			log.error(e.getMessage(), e);
		}
	}

	public List<Map<String, String>> searchCountByKeywords(String keyword)
	{
		try
		{
			int returnCount = 10;
			List<Map<String, String>> result = new ArrayList<Map<String, String>>();
			if (StringUtils.isNotBlank(keyword))
			{
				WildcardQuery query = new WildcardQuery(new Term("name", "*" + keyword + "*"));
				TopFieldDocs docs = categorySearcher.search(query, returnCount, prioritySort);
				ScoreDoc[] hits = docs.scoreDocs;
				for (int i = 0; i < hits.length; i++)
				{
					Document hitDoc = categorySearcher.doc(hits[i].doc);
					Map<String, String> row = new HashMap<String, String>();
					row.put("name", hitDoc.get("name"));
					row.put("count", hitDoc.get("count"));
					result.add(row);
				}
			}

			if (result.size() < returnCount)
			{
				WildcardQuery query = new WildcardQuery(new Term("name", "*" + keyword + "*"));
				TopFieldDocs docs = searcher.search(query, returnCount - result.size(), prioritySort);
				ScoreDoc[] hits = docs.scoreDocs;
				for (int i = 0; i < hits.length; i++)
				{
					Document hitDoc = searcher.doc(hits[i].doc);
					Map<String, String> row = new HashMap<String, String>();
					row.put("name", hitDoc.get("name"));
					row.put("count", "1");
					result.add(row);
				}
			}
			return result;
		}
		catch (Exception e)
		{
			log.warn("unable to search : " + e.getMessage());
		}
		return null;
	}

	public Page doSearch(String category, List<SearchCondition> conditions, Page page)
	{
		List<String> result = new ArrayList<String>();
		BooleanQuery query = new BooleanQuery();
		if (StringUtils.isBlank(category))
			return null;
		query.add(new TermQuery(new Term("category", category)), BooleanClause.Occur.MUST);
		if (conditions != null)
		{
			for (SearchCondition condition : conditions)
			{
				if (condition.getValue() == null || condition.getValue().length < 1)
					continue;
				BooleanQuery subQuery = new BooleanQuery();
				for (String value : condition.getValue())
				{
					if (condition.getType() == SearchCondition.TYPE_OR)
						subQuery.add(new TermQuery(new Term(condition.getName(), value)), BooleanClause.Occur.SHOULD);
					else
						subQuery.add(new TermQuery(new Term(condition.getName(), value)), BooleanClause.Occur.MUST);
				}
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
		}
		try
		{
			TopFieldDocs docs = searcher.search(query, null, 100,
					getSort(page.getOrderBy(), Page.DESC.equals(page.getOrder())));
			page.setTotalCount(docs.totalHits);
			ScoreDoc[] hits = docs.scoreDocs;
			int begin = page.getFirst() - 1;
			int end = Math.min(begin + page.getPageSize(), hits.length);
			for (; begin < end; begin++)
			{
				Document hitDoc = searcher.doc(hits[begin].doc);
				String id = hitDoc.get("goodsId");
				result.add(id);
			}
			page.setResult(result);
		}
		catch (IOException e)
		{
			log.warn("unable to search : " + query.toString() + ", " + e.getMessage());
		}
		return page;
	}

	public String[] doSearch(String merchantId, String keyword, Page page)
	{
		String merchantIds = null;
		String typeIds = null;
		boolean matchMerchant = false;
		try
		{
			if (StringUtils.isNotBlank(keyword))
			{
				TermQuery query = new TermQuery(new Term("name", keyword));
				TopFieldDocs docs = categorySearcher.search(query, 1, prioritySort);
				ScoreDoc[] hits = docs.scoreDocs;
				if (hits.length > 0)
				{
					Document hitDoc = categorySearcher.doc(hits[0].doc);
					typeIds = hitDoc.get("typeIds");
					merchantIds = hitDoc.get("merchantIds");
					if ("1".equals(hitDoc.get("type")))
						matchMerchant = true;
				}
			}

			BooleanQuery query = new BooleanQuery();
			if (StringUtils.isNotBlank(merchantId))
				query.add(new TermQuery(new Term("merchantId", merchantId)), BooleanClause.Occur.MUST);
			if (matchMerchant)
			{
				BooleanQuery subQuery = new BooleanQuery();
				for (String merchant : merchantIds.split(" "))
					subQuery.add(new TermQuery(new Term("merchantId", merchant)), BooleanClause.Occur.SHOULD);
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
			if (StringUtils.isNotBlank(typeIds))
			{
				BooleanQuery subQuery = new BooleanQuery();
				for (String typeId : typeIds.split(" "))
				{
					subQuery.add(new TermQuery(new Term("category", typeId)), BooleanClause.Occur.SHOULD);
					subQuery.add(new TermQuery(new Term("typeIds", typeId)), BooleanClause.Occur.SHOULD);
				}
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
			else if (!matchMerchant)
			{
				BooleanQuery subQuery = new BooleanQuery();
				BooleanQuery nameQuery = new BooleanQuery();
				BooleanQuery keywordsQuery = new BooleanQuery();
				if (keyword.contains(" "))
				{
					String[] keywords = keyword.split(" ");
					for (String key : keywords)
					{
						nameQuery.add(new WildcardQuery(new Term("name", "*" + key + "*")), BooleanClause.Occur.MUST);
						keywordsQuery.add(new WildcardQuery(new Term("keywords", "*" + key + "*")),
								BooleanClause.Occur.MUST);
					}
				}
				else
				{
					nameQuery.add(new WildcardQuery(new Term("name", "*" + keyword + "*")), BooleanClause.Occur.MUST);
					keywordsQuery.add(new WildcardQuery(new Term("keywords", "*" + keyword + "*")),
							BooleanClause.Occur.MUST);
				}
				subQuery.add(nameQuery, BooleanClause.Occur.SHOULD);
				subQuery.add(keywordsQuery, BooleanClause.Occur.SHOULD);
				query.add(subQuery, BooleanClause.Occur.MUST);
			}
			TopFieldDocs docs = searcher.search(query, null, 100,
					getSort(page.getOrderBy(), Page.DESC.equals(page.getOrder())));
			page.setTotalCount(docs.totalHits);
			ScoreDoc[] hits = docs.scoreDocs;
			List<String> result = new ArrayList<String>();
			int begin = page.getFirst() - 1;
			int end = Math.min(begin + page.getPageSize(), hits.length);
			for (; begin < end; begin++)
			{
				Document hitDoc = searcher.doc(hits[begin].doc);
				String id = hitDoc.get("goodsId");
				result.add(id);
			}
			page.setResult(result);
		}
		catch (IOException e)
		{
			log.warn("unable to search : " + keyword + ", " + e.getMessage());
		}
		return StringUtils.isNotBlank(merchantIds) && !matchMerchant ? merchantIds.split(" ") : null;
	}

	/**
	 * 根据goodsId查询该险种(类别category)信息
	 * 
	 * @param goodsId
	 * @return
	 */
	public String[] searchByGoodsId(String goodsId)
	{
		// QueryParser parser = new QueryParser(Version.LUCENE_30, "goodsId", new
		// StandardAnalyzer(Version.LUCENE_30));
		String cate[] = null;
		BooleanQuery query = new BooleanQuery();
		if (StringUtils.isBlank(goodsId))
			return null;
		query.add(new TermQuery(new Term("goodsId", goodsId)), BooleanClause.Occur.MUST);
		try
		{
			// TopFieldDocs docs = searcher.search(query, null, 100, salesSort);
			TopDocs docs = searcher.search(query, 1);
			ScoreDoc[] hit = docs.scoreDocs;
			if (hit.length > 0)
			{
				Document hitdoc = searcher.doc(hit[0].doc);
				String category = hitdoc.get("category");
				cate = category.split(" ");
			}

		}
		catch (CorruptIndexException e)
		{
			log.warn("unable to search : " + query.toString() + ", " + e.getMessage());
		}
		catch (IOException e)
		{
			log.warn("unable to search : " + query.toString() + ", " + e.getMessage());
		}
		if (cate == null)
		{
			return null;
		}
		return cate;
	}

	/**
	 * 根据type查找所属的所有的goodsId
	 * 
	 * @param typeId
	 * @return
	 */
	@Cacheable
	public List<String> getGoodsIdsByCategory(String category)
	{
		List<String> result = new ArrayList<String>();
		BooleanQuery query = new BooleanQuery();
		if (StringUtils.isBlank(category))
			return null;
		String categoryId = category;
		if (!StringUtils.isNumeric(category))
		{
			SearchType type = searchTypeService.getSearchTypeByName(category, null);
			if (type == null)
				return null;
			categoryId = type.getId();
		}

		query.add(new TermQuery(new Term("category", categoryId)), BooleanClause.Occur.MUST);
		try
		{
			TopDocs docs = searcher.search(query, 100);
			ScoreDoc[] hits = docs.scoreDocs;
			for (int i = 0; i < hits.length; i++)
			{
				Document hitDoc = searcher.doc(hits[i].doc);
				String id = hitDoc.get("goodsId");
				result.add(id);
			}
			// 历史遗留车险goodsId
			if ("car".equals(category))
			{
				result.add("6101");
				result.add("6102");
				result.add("6103");
				result.add("6104");
			}
		}
		catch (IOException e)
		{
			log.warn("unable to search : " + query.toString() + ", " + e.getMessage());
		}
		return result;
	}

	public void addLogoSelesCount(List<Map> goodsList)
	{
		if (!goodsList.isEmpty())
		{
			for (Map map : goodsList)
			{
				String merchantId = map.get("MERCHANTID").toString();
				String iniValue = IniBean.getIniValue("merchant_logo_config", "{}");
				JSONObject json = JSONObject.fromObject(iniValue);

				if (json.get(merchantId) != null)
				{
					map.put("logo", json.getString(merchantId));
				}
				else
				{
					map.put("logo", json.getString("default"));
				}
				Object id = map.get("ID");
				Object count = map.get("SALESCOUNT");
				GoodsInfo goodsInfo = shopGoodsService.getCacheGoodsById(Integer.valueOf(id.toString()));
				map.put("SALESCOUNT", goodsInfo.getSaleCount());
				/*
				 * String count = map.get("SALESCOUNT").toString(); Integer temp = Integer.valueOf(count) *
				 * 10; map.put("SALESCOUNT", temp.toString());
				 */
			}
		}
	}
}
