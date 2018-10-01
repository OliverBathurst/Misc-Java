/**
 * This class represents an example usage of the TreeBuilder class
 */

public class Main {

	public static void main(String[] args){
		TreeBuilder treeBuilder = new TreeBuilder();
		INode node1 = new Node();
		node1.setNodeName("Node 1");
		INode node2 = new Node();
		node2.setNodeName("Node 2");
		INode node3 = new Node();
		node3.setNodeName("Node 3");
		
		INode subNode1 = new Node();
		subNode1.setNodeName("Sub Node 1");
		
		INode subNode2 = new Node();
		subNode2.setNodeName("Sub Node 2");
		
		INode subNode3 = new Node();
		subNode3.setNodeName("Sub Node 3");
		
		INode subNode4 = new Node();
		subNode4.setNodeName("Sub Node 4");
		
		INode subNode5 = new Node();
		subNode5.setNodeName("Sub Node 5");
		subNode4.addNode(subNode5);
		
		node1.addNode(subNode1);
		node2.addNode(subNode1);
		node2.addNode(subNode2);
		node3.addNode(subNode1);
		node3.addNode(subNode2);
		node3.addNode(subNode3);
		
		treeBuilder.addNode(node1);
		treeBuilder.addNode(node2);
		treeBuilder.addNode(node3);
		
		treeBuilder.getNode(2).getNode(2).addNode(subNode4);
		
		treeBuilder.printTree();
	}
}