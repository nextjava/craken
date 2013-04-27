package net.ion.craken.node.crud;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;

import net.ion.craken.node.IteratorList;
import net.ion.craken.node.ReadInNode;
import net.ion.craken.node.ReadNode;
import net.ion.craken.node.ReadSession;
import net.ion.craken.tree.Fqn;
import net.ion.craken.tree.PropertyId;
import net.ion.craken.tree.PropertyValue;
import net.ion.craken.tree.TreeNode;
import net.ion.framework.parse.gson.JsonArray;
import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.ListUtil;
import net.ion.framework.util.ObjectUtil;
import net.ion.framework.util.SetUtil;

import com.google.common.base.Optional;

public class ReadNodeImpl implements ReadNode{

	private ReadSession session ;
	private TreeNode<PropertyId, PropertyValue> tree;
	protected ReadNodeImpl(ReadSession session, TreeNode<PropertyId, PropertyValue> inner) {
		this.session = session ;
		this.tree = inner ;
	}

	public static ReadNode load(ReadSession session, TreeNode<PropertyId, PropertyValue> inner) {
		return new ReadNodeImpl(session, inner);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof ReadNodeImpl)) return false ;
		ReadNodeImpl that = (ReadNodeImpl) obj ;
		return tree.equals(that.tree) ;
	}
	
	@Override
	public int hashCode(){
		return tree.hashCode() ;
	}
	
	public String toString(){
		return this.getClass().getSimpleName() + "[fqn=" + tree.getFqn().toString() + "]";
	}

	
	
	// .. common 
	
	public Fqn fqn(){
		return tree.getFqn() ;
	}
	
	public int dataSize(){
		return tree.dataSize() ;
	}

	public ReadNode parent(){
		return load(session, tree.getParent()) ;
	}
	
	public boolean hasChild(String relativeFqn){
		return tree.hasChild(Fqn.fromString(relativeFqn)) ;
	}
	
	public ReadNode child(String fqn){
		final TreeNode child = tree.getChild(Fqn.fromString(fqn));
		if (child == null) throw new IllegalArgumentException("not found child : " + fqn) ; 
		return load(session, child) ;
	}
	
	public Set<Object> childrenNames(){
		return tree.getChildrenNames() ;
	}
	
	public IteratorList<ReadNode> children(){
		
		final Iterator<TreeNode<PropertyId, PropertyValue>> iter = tree.getChildren().iterator();
		return new IteratorList<ReadNode>() {
			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public ReadNode next() {
				return ReadNodeImpl.this.load(session, iter.next());
			}

			@Override
			public void remove() {
				iter.remove() ;
			}
			
			public List<ReadNode> toList(){
				List<ReadNode> result = ListUtil.newList() ;
				while(hasNext()){
					result.add(next()) ;
				}
				return result ;
			}
		};
	}

	public PropertyValue property(String key) {
		return property(PropertyId.normal(key)) ;
	}

	public PropertyValue property(PropertyId pid) {
		return ObjectUtil.coalesce(tree.get(pid), PropertyValue.NotFound);
	}

	public Optional<PropertyValue> optional(String key) {
		return Optional.fromNullable(tree.get(PropertyId.normal(key)));
	}
	

	public Set<PropertyId> keys(){
		return tree.getKeys() ;
	}

	public Map<PropertyId, PropertyValue> toMap() {
		return tree.getData();
	}
	
	public Object id(){
		return tree.getFqn() ;
	}
	
	private boolean containsProperty(PropertyId pid){
		return keys().contains(pid) ;
	}
	
	public ReadNode ref(String refName){
		PropertyId referId = PropertyId.refer(refName);
		if (containsProperty(referId)) {
			Object val = property(referId).value() ;
			return val == null ? null : session.pathBy(val.toString()) ;
		} else {
			return null;
		}
	}
	
	public IteratorList<ReadNode> refs(String refName){
		
		PropertyId referId = PropertyId.refer(refName);
		final Iterator<String> iter = containsProperty(referId) ? property(referId).asSet().iterator() : IteratorUtils.EMPTY_ITERATOR;
		
		return new IteratorList<ReadNode>() {
			@Override
			public List<ReadNode> toList() {
				List<ReadNode> result = ListUtil.newList() ;
				while(iter.hasNext()) {
					result.add(session.pathBy(iter.next())) ;
				}
				return Collections.unmodifiableList(result);
			}

			@Override
			public boolean hasNext() {
				return iter.hasNext();
			}

			@Override
			public ReadNode next() {
				return session.pathBy(iter.next());
			}

			@Override
			public void remove() {
				iter.remove() ;
			}
		};
	}
	
	public <T> T toBean(Class<T> clz){
		return JsonParser.fromObject(tree.getData()).getAsJsonObject().getAsObject(clz) ;
		
	}

}
