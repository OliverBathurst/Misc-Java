import java.util.ArrayList;
import java.util.Collections;

public class TreeBuilder {
	private ArrayList<INode> nodeList;
	
	TreeBuilder(){
		this.nodeList = new ArrayList<>();
	}
	
	boolean addNode(INode node) {
		return this.nodeList.add(node);
	}
	
	boolean removeNode(INode node) {
		return this.nodeList.remove(node);
	}
	
	INode removeNode(int nodeIndex) {
		return this.nodeList.remove(nodeIndex);
	}
	
	INode getNode(int nodeIndex) {
		return this.nodeList.get(nodeIndex);
	}
	
	void printTree() {
		for(INode node : nodeList) {
			System.out.println("-" + node.getNodeName());
			for(INode innerNode : node.getNodes()) {
				printNode(innerNode, 1);
			}
		}
	}
	
	private void printNode(INode node, int depth) {
		System.out.println(String.join("", Collections.nCopies(depth, "\t")) 
				+ String.join("", Collections.nCopies(depth, "--"))
				+ node.getNodeName());
		for(INode nodes : node.getNodes()) {
			printNode(nodes, depth + 1);
		}
	}
}
