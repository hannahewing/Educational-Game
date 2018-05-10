package Game;


public class HeapDriver
{
	public static void main(String [] args)
	  {
	      
	      
	    //Create the heap
	    Heap h = new Heap();

	    //Insert some items
	    h.insert(new Integer(5),4);
	    h.insert(new Integer(3),7);
	    h.insert(new Integer(7),3);
	    h.insert(new Integer(1),8);
	    h.insert(new Integer(6),9);
	    h.insert(new Integer(2),6);
	    h.insert(new Integer(10),5);


	    System.out.println("Here is the heap: "+ h);
	    
	    System.out.println("getting ready to deplete the tree");

	    while (!h.isEmpty())
	    {
	        HeapEntry node = (HeapEntry) h.remove();
	        System.out.println(node.getPriority());
	    }
	    //Display the heap

	  }
}//HeapDriver
