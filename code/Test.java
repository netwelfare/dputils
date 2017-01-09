
public class Test
{

	public Test()
	{
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args)
	{
		Base a = new Sub1();
		Base b = new Sub2();

		System.out.println(a instanceof Base);
		System.out.println(b instanceof Base);
	}

}
