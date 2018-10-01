import java.util.AbstractList;

interface INode {
	AbstractList<INode> getNodes();
	void setNodeValue(Object o);
	boolean addNode(INode node);
	boolean equals(INode node);
	void setNodeName(String name);
	INode getNode(INode nodeIndex);
	INode getNode(int nodeIndex);
	Object getNodeValue();
	String getNodeName();
}
