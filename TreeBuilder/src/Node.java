import java.util.AbstractList;
import java.util.ArrayList;

public class Node implements INode{
	private String nodeName = "Default NodeName";
	private AbstractList<INode> nodeList;
	private Object value;
	
	Node(){
		this.nodeList = new ArrayList<>();
	}

	@Override
	public boolean addNode(INode node) {
		return this.nodeList.add(node);
	}

	@Override
	public INode getNode(int nodeIndex) {
		return this.nodeList.get(nodeIndex);
	}

	@Override
	public INode getNode(INode node) {
		return this.nodeList.get(nodeList.indexOf(node));
	}

	@Override
	public void setNodeValue(Object o) {
		this.value = o;
	}

	@Override
	public Object getNodeValue() {
		return this.value;
	}

	@Override
	public void setNodeName(String name) {
		this.nodeName = name;
	}

	@Override
	public String getNodeName() {
		return this.nodeName;
	}

	@Override
	public AbstractList<INode> getNodes() {
		return this.nodeList;
	}

	@Override
	public boolean equals(INode node) {
		return this.value == node.getNodeValue()
				&& this.nodeList.equals(node.getNodes());
	}
}