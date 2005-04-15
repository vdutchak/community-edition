package com.activiti.repo.search.impl.lucene;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.activiti.repo.dictionary.AspectDefinition;
import com.activiti.repo.dictionary.AssociationDefinition;
import com.activiti.repo.dictionary.ClassDefinition;
import com.activiti.repo.dictionary.ClassRef;
import com.activiti.repo.dictionary.DictionaryService;
import com.activiti.repo.dictionary.NamespaceService;
import com.activiti.repo.dictionary.PropertyDefinition;
import com.activiti.repo.dictionary.PropertyRef;
import com.activiti.repo.dictionary.TypeDefinition;
import com.activiti.repo.dictionary.bootstrap.DictionaryBootstrap;
import com.activiti.repo.node.AssociationExistsException;
import com.activiti.repo.node.InvalidNodeRefException;
import com.activiti.repo.node.NodeService;
import com.activiti.repo.ref.ChildAssocRef;
import com.activiti.repo.ref.EntityRef;
import com.activiti.repo.ref.NamespaceException;
import com.activiti.repo.ref.NodeRef;
import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.QName;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.ResultSetRow;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.search.Value;
import com.activiti.repo.search.transaction.LuceneIndexLock;

/**
 * @author andyh
 * 
 */
public class LuceneTest extends TestCase
{

    public LuceneTest()
    {
        super();

    }

    public LuceneTest(String arg0)
    {
        super(arg0);
    }

    /**
     * Test basic index and search
     * 
     */

    public void testStandAloneIndexerCommit()
    {

        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(NodeServiceStub.storeRef, "delta" + System.currentTimeMillis());
        indexer.setNodeService(new NodeServiceStub());
        indexer.setLuceneIndexLock(new LuceneIndexLock());
        indexer.setDictionaryService(new MockDictionaryService());

        indexer.createNode(new ChildAssocRef(null, null, NodeServiceStub.rootNode));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{namespace}one"), NodeServiceStub.n1));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{namespace}two"), NodeServiceStub.n2));
        indexer.updateNode(NodeServiceStub.n1);
        // indexer.deleteNode(new ChildRelationshipRef(rootNode, "path",
        // newNode));

        indexer.commit();

        Searcher searcher = LuceneSearcherImpl.getSearcher(NodeServiceStub.storeRef);

        
        ResultSet results = searcher.query(NodeServiceStub.storeRef, "lucene", "\\@\\{namespace\\}property\\-2:\"value-2\"", null, null);
        assertEquals(1, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "\\@\\{namespace\\}property\\-1:\"value-1\"", null, null);
        assertEquals(2, results.length());
        assertEquals("2", results.getNodeRef(0).getId());
        assertEquals("1", results.getNodeRef(1).getId());
        assertEquals(1.0f, results.getScore(0));
        assertEquals(1.0f, results.getScore(1));

        QName qname = QName.createQName("", "property-1");

        for (ResultSetRow row : results)
        {
            System.out.println("Node = " + row.getNodeRef() + " score " + row.getScore());
            System.out.println("QName <" + qname + "> = " + row.getValue(qname));
            System.out.print("\t");
            Value[] values = row.getValues();
            for (Value value : values)
            {
                System.out.print("<");
                System.out.print(value);
                System.out.print(">");
            }
            System.out.println();

        }

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "ID:\"1\"", null, null);
        assertEquals(2, results.length());

    }

    public void testStandAlonePathIndexer()
    {
        LuceneIndexerImpl indexer = LuceneIndexerImpl.getUpdateIndexer(NodeServiceStub.storeRef, "delta" + System.currentTimeMillis());
        indexer.setNodeService(new NodeServiceStub());
        indexer.setLuceneIndexLock(new LuceneIndexLock());
        indexer.setDictionaryService(new MockDictionaryService());

        indexer.createNode(new ChildAssocRef(null, null, NodeServiceStub.rootNode));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{namespace}one"), NodeServiceStub.n1));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{namespace}two"), NodeServiceStub.n2));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{namespace}three"), NodeServiceStub.n3));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.rootNode, QName.createQName("{namespace}four"), NodeServiceStub.n4));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n1, QName.createQName("{namespace}five"), NodeServiceStub.n5));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n1, QName.createQName("{namespace}six"), NodeServiceStub.n6));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n2, QName.createQName("{namespace}seven"), NodeServiceStub.n7));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n2, QName.createQName("{namespace}eight"), NodeServiceStub.n8));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{namespace}nine"), NodeServiceStub.n9));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{namespace}ten"), NodeServiceStub.n10));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{namespace}eleven"), NodeServiceStub.n11));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n5, QName.createQName("{namespace}twelve"), NodeServiceStub.n12));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n12, QName.createQName("{namespace}thirteen"), NodeServiceStub.n13));
        indexer.createNode(new ChildAssocRef(NodeServiceStub.n13, QName.createQName("{namespace}fourteen"), NodeServiceStub.n14));

        // indexer.deleteNode(new ChildRelationshipRef(rootNode, "path",
        // newNode));

        indexer.commit();

        Searcher searcher = LuceneSearcherImpl.getSearcher(NodeServiceStub.storeRef);

        ResultSet results = searcher.query(NodeServiceStub.storeRef, "lucene", "@\\{namespace\\}property-1:value-1", null, null);
        assertEquals(2, results.length());
        assertEquals("1", results.getNodeRef(0).getId());
        assertEquals("2", results.getNodeRef(1).getId());
        assertEquals(1.0f, results.getScore(0));
        assertEquals(1.0f, results.getScore(1));

        QName qname = QName.createQName("", "property-1");

        for (ResultSetRow row : results)
        {
            System.out.println("Node = " + row.getNodeRef() + " score " + row.getScore());
            System.out.println("QName <" + qname + "> = " + row.getValue(qname));
            System.out.print("\t");
            Value[] values = row.getValues();
            for (Value value : values)
            {
                System.out.print("<");
                System.out.print(value);
                System.out.print(">");
            }
            System.out.println();

        }

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "+ID:\"1\"", null, null);
        assertEquals(2, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "ID:\"0\"", null, null);
        assertEquals(1, results.length());

    }

    /**
     * Test thread local transactions and indexing
     * 
     */
    public void xtestThreadLocalTXIndexerCommit()
    {

        StoreRef storeRef1 = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws1");
        NodeRef newNode1 = new NodeRef(storeRef1, "1");
        NodeRef rootNode1 = new NodeRef(storeRef1, "0");

        StoreRef storeRef2 = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws2");
        NodeRef newNode2 = new NodeRef(storeRef2, "1");
        NodeRef rootNode2 = new NodeRef(storeRef2, "0");

        LuceneIndexer indexer1 = LuceneIndexerAndSearcherFactory.getInstance().getIndexer(storeRef1);
        indexer1.setNodeService(new NodeServiceStub());

        LuceneIndexer indexer2 = LuceneIndexerAndSearcherFactory.getInstance().getIndexer(storeRef2);
        indexer2.setNodeService(new NodeServiceStub());

        indexer1.createNode(new ChildAssocRef(rootNode1, QName.createQName("{namespace}path"), newNode1));
        indexer1.updateNode(newNode1);
        indexer1.deleteNode(new ChildAssocRef(rootNode1, QName.createQName("{namespace}path"), newNode1));

        indexer2.createNode(new ChildAssocRef(rootNode2, QName.createQName("{namespace}path"), newNode2));
        indexer2.updateNode(newNode2);
        indexer2.deleteNode(new ChildAssocRef(rootNode2, QName.createQName("{namespace}path"), newNode2));

        LuceneIndexerAndSearcherFactory.getInstance().commit();

    }

    public void testAllPathSearch()
    {
        Searcher searcher = LuceneSearcherImpl.getSearcher(NodeServiceStub.storeRef);
        searcher.setNameSpaceService(new MockNameService());
        
        // Single

        ResultSet results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:three\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:four\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:eight-0\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:five\"", null, null);
        assertEquals(0, results.length());

        // Double

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:one\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:two\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two/namespace:one\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two/namespace:two\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:five\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:six\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two/namespace:seven\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:eight-1\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two/namespace:eight-2\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:eight-2\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two/namespace:eight-1\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:two/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:eight-0\"", null, null);
        assertEquals(0, results.length());

        // Three

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:five/namespace:nine\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:five/namespace:ten\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:five/namespace:eleven\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:five/namespace:twelve\"", null, null);
        assertEquals(1, results.length());

        // namespace:*

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:*\"", null, null);
        assertEquals(5, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:*/namespace:*\"", null, null);
        assertEquals(8, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:*/namespace:five\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:*/namespace:*/namespace:*\"", null, null);
        assertEquals(8, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:*\"", null, null);
        assertEquals(4, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:*/namespace:five/namespace:*\"", null, null);
        assertEquals(5, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:*/namespace:nine\"", null, null);
        assertEquals(1, results.length());


        // *

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/*\"", null, null);
        assertEquals(5, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/*/*\"", null, null);
        assertEquals(8, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/*/namespace:five\"", null, null);
        assertEquals(1, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/*/*/*\"", null, null);
        assertEquals(8, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/*\"", null, null);
        assertEquals(4, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/*/namespace:five/*\"", null, null);
        assertEquals(5, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/*/namespace:nine\"", null, null);
        assertEquals(1, results.length());

        // //.

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//.\"", null, null);
        assertEquals(26, results.length());

        // //*

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//*\"", null, null);
        assertEquals(25, results.length());

        // //*/.

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//*/.\"", null, null);
        assertEquals(25, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//*/./.\"", null, null);
        assertEquals(25, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//./*\"", null, null);
        assertEquals(25, results.length());
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//././*/././.\"", null, null);
        assertEquals(25, results.length());

    }

    public void testPathSearch()
    {
        Searcher searcher = LuceneSearcherImpl.getSearcher(NodeServiceStub.storeRef);
        searcher.setNameSpaceService(new MockNameService());

        // //*

        ResultSet
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/namespace:one/namespace:five\"", null, null);
        assertEquals(1, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/*/*\"", null, null);
        assertEquals(8, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"//common\"", null, null);
        assertEquals(7, results.length());

        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/one//common\"", null, null);
        assertEquals(5, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/one/five//*\"", null, null);
        assertEquals(9, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/one/five//.\"", null, null);
        printPaths(results);
        assertEquals(10, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/one//five/nine\"", null, null);
        printPaths(results);
        assertEquals(1, results.length());
        
        results = searcher.query(NodeServiceStub.storeRef, "lucene", "PATH:\"/one//thirteen/fourteen\"", null, null);
        printPaths(results);
        assertEquals(1, results.length());   
    }

    void printPaths(ResultSet results)
    {
        NodeService ns = new NodeServiceStub();

        for (ResultSetRow row : results)
        {
            System.out.println(row.getNodeRef());
            for (Path path : ns.getPaths(row.getNodeRef(), false))
            {
                System.out.println("\t" + path);
            }
        }
    }

    public void testXPathSearch()
    {
        Searcher searcher = LuceneSearcherImpl.getSearcher(NodeServiceStub.storeRef);
        searcher.setNameSpaceService(new MockNameService());

        // //*

        ResultSet

        results = searcher.query(NodeServiceStub.storeRef, "xpath", "//./*", null, null);
        assertEquals(25, results.length());
    }
    
    public void testMissingIndex()
    {
        StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "_missing_");
        Searcher searcher = LuceneSearcherImpl.getSearcher(storeRef);
        searcher.setNameSpaceService(new MockNameService());

        // //*

        ResultSet

        results = searcher.query(NodeServiceStub.storeRef, "xpath", "//./*", null, null);
        assertEquals(0, results.length());
    }

    /**
     * Support for DummyNodeService
     * 
     * @author andyh
     * 
     */

    private static class NodeServiceStub implements NodeService
    {

        static StoreRef storeRef = new StoreRef(StoreRef.PROTOCOL_WORKSPACE, "ws");

        static NodeRef rootNode = new NodeRef(storeRef, "0");

        static NodeRef n1 = new NodeRef(storeRef, "1");

        static NodeRef n2 = new NodeRef(storeRef, "2");

        static NodeRef n3 = new NodeRef(storeRef, "3");

        static NodeRef n4 = new NodeRef(storeRef, "4");

        static NodeRef n5 = new NodeRef(storeRef, "5");

        static NodeRef n6 = new NodeRef(storeRef, "6");

        static NodeRef n7 = new NodeRef(storeRef, "7");

        static NodeRef n8 = new NodeRef(storeRef, "8");

        static NodeRef n9 = new NodeRef(storeRef, "9");

        static NodeRef n10 = new NodeRef(storeRef, "10");

        static NodeRef n11 = new NodeRef(storeRef, "11");

        static NodeRef n12 = new NodeRef(storeRef, "12");

        static NodeRef n13 = new NodeRef(storeRef, "13");

        static NodeRef n14 = new NodeRef(storeRef, "14");

        public StoreRef createStore(String protocol, String identifier)
        {
            throw new UnsupportedOperationException();
        }

        public boolean exists(StoreRef storeRef)
        {
            throw new UnsupportedOperationException();
        }

        public boolean exists(NodeRef nodeRef)
        {
            throw new UnsupportedOperationException();
        }

        public NodeRef getRootNode(StoreRef storeRef)
        {
            throw new UnsupportedOperationException();
        }

        public ClassRef getType(NodeRef nodeRef)
        {
            if (nodeRef.getId().equals("0"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("1"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("2"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("3"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else if (nodeRef.getId().equals("4"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else if (nodeRef.getId().equals("5"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("6"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("7"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else if (nodeRef.getId().equals("8"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("9"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else if (nodeRef.getId().equals("10"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else if (nodeRef.getId().equals("11"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else if (nodeRef.getId().equals("12"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("13"))
            {
                return DictionaryBootstrap.TYPE_FOLDER;
            }
            else if (nodeRef.getId().equals("14"))
            {
                return DictionaryBootstrap.TYPE_FILE;
            }
            else
            {
                throw new InvalidNodeRefException(nodeRef);
            }
        }

        public Map<QName, Serializable> getProperties(NodeRef nodeRef) throws InvalidNodeRefException
        {
            Map<QName, Serializable> answer = new HashMap<QName, Serializable>();
            answer.put(QName.createQName("{namespace}createby"), "andy");
            if (nodeRef.getId().equals("0"))
            {
                answer.put(QName.createQName("{namespace}does-a-property-on-the-root-make-sense"), "no");
            }
            if (nodeRef.getId().equals("1"))
            {
                answer.put(QName.createQName("{namespace}property-1"), "value-1");
            }
            else if (nodeRef.getId().equals("2"))
            {
                answer.put(QName.createQName("{namespace}property-1"), "value-1");
                answer.put(QName.createQName("{namespace}property-2"), "value-2");
            }
            else if (nodeRef.getId().equals("3"))
            {

            }
            else if (nodeRef.getId().equals("4"))
            {

            }
            else if (nodeRef.getId().equals("5"))
            {

            }
            else if (nodeRef.getId().equals("6"))
            {

            }
            else if (nodeRef.getId().equals("7"))
            {

            }
            else if (nodeRef.getId().equals("8"))
            {

            }
            else if (nodeRef.getId().equals("9"))
            {

            }
            else if (nodeRef.getId().equals("10"))
            {

            }
            else if (nodeRef.getId().equals("11"))
            {

            }
            else if (nodeRef.getId().equals("12"))
            {

            }
            return answer;
        }

        public Collection<NodeRef> getParents(NodeRef nodeRef) throws InvalidNodeRefException
        {
            ArrayList<NodeRef> parents = new ArrayList<NodeRef>();
            if (nodeRef.getId().equals("0"))
            {
               
            }
            if (nodeRef.getId().equals("1"))
            {
                parents.add(rootNode);
            }
            else if (nodeRef.getId().equals("2"))
            {
                parents.add(rootNode);
            }
            else if (nodeRef.getId().equals("3"))
            {
                parents.add(rootNode);
            }
            else if (nodeRef.getId().equals("4"))
            {
                parents.add(rootNode);
            }
            else if (nodeRef.getId().equals("5"))
            {
                parents.add(n1);
            }
            else if (nodeRef.getId().equals("6"))
            {
                parents.add(n1);
            }
            else if (nodeRef.getId().equals("7"))
            {
                parents.add(n2);
            }
            else if (nodeRef.getId().equals("8"))
            {
                parents.add(rootNode);
                parents.add(n1);
                parents.add(n2);
            }
            else if (nodeRef.getId().equals("9"))
            {
                parents.add(n5);
            }
            else if (nodeRef.getId().equals("10"))
            {
                parents.add(n5);
            }
            else if (nodeRef.getId().equals("11"))
            {
                parents.add(n5);
            }
            else if (nodeRef.getId().equals("12"))
            {
                parents.add(n5);
            }
            else if (nodeRef.getId().equals("13"))
            {
                parents.add(n2);
                parents.add(n12);
            }
            else if (nodeRef.getId().equals("14"))
            {
                parents.add(n1);
                parents.add(n2);
                parents.add(n5);
                parents.add(n6);
                parents.add(n12);
                parents.add(n13);
            }
            return parents;
        }

        public ChildAssocRef createNode(NodeRef parentRef, QName qname, ClassRef typeRef) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public ChildAssocRef createNode(NodeRef parentRef, QName qname, ClassRef typeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public void deleteNode(NodeRef nodeRef) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public ChildAssocRef addChild(NodeRef parentRef, NodeRef childRef, QName qname) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Collection<EntityRef> removeChild(NodeRef parentRef, NodeRef childRef) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Collection<EntityRef> removeChildren(NodeRef parentRef, QName qname) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Serializable getProperty(NodeRef nodeRef, QName qname) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public void setProperty(NodeRef nodeRef, QName qame, Serializable value) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Collection<ChildAssocRef> getChildAssocs(NodeRef nodeRef) throws InvalidNodeRefException
        {
            ArrayList<ChildAssocRef> assocs = new ArrayList<ChildAssocRef>();
            if (nodeRef.getId().equals("0"))
            {
                assocs.add(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1));
                assocs.add(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2));
                assocs.add(new ChildAssocRef(rootNode, QName.createQName("namespace", "three"), n3));
                assocs.add(new ChildAssocRef(rootNode, QName.createQName("namespace", "four"), n4));
                assocs.add(new ChildAssocRef(rootNode, QName.createQName("namespace", "eight-0"), n8));
            }
            else if(nodeRef.getId().equals("1"))
            {
                assocs.add(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5));
                assocs.add(new ChildAssocRef(n1, QName.createQName("namespace", "six"), n6));
                assocs.add(new ChildAssocRef(n1, QName.createQName("namespace", "eight-1"), n8));
                assocs.add(new ChildAssocRef(n1, QName.createQName("namespace", "common"), n14));
            }
            else if(nodeRef.getId().equals("2"))
            {
                assocs.add(new ChildAssocRef(n2, QName.createQName("namespace", "seven"), n7));
                assocs.add(new ChildAssocRef(n2, QName.createQName("namespace", "eight-2"), n8));
                assocs.add(new ChildAssocRef(n2, QName.createQName("namespace", "link"), n13));
                assocs.add(new ChildAssocRef(n2, QName.createQName("namespace", "common"), n14));
            }
            else if(nodeRef.getId().equals("5"))
            {
                assocs.add(new ChildAssocRef(n5, QName.createQName("namespace", "nine"), n9));
                assocs.add(new ChildAssocRef(n5, QName.createQName("namespace", "ten"), n10));
                assocs.add(new ChildAssocRef(n5, QName.createQName("namespace", "eleven"), n11));
                assocs.add(new ChildAssocRef(n5, QName.createQName("namespace", "twelve"), n12));
                assocs.add(new ChildAssocRef(n5, QName.createQName("namespace", "common"), n14));
            }
            else if(nodeRef.getId().equals("6"))
            {
                assocs.add(new ChildAssocRef(n6, QName.createQName("namespace", "common"), n14));
            }
            else if(nodeRef.getId().equals("12"))
            {
                assocs.add(new ChildAssocRef(n12, QName.createQName("namespace", "thirteen"), n13));
                assocs.add(new ChildAssocRef(n12, QName.createQName("namespace", "common"), n14));
            }
            else if(nodeRef.getId().equals("13"))
            {
                assocs.add(new ChildAssocRef(n13, QName.createQName("namespace", "fourteen"), n14));
                assocs.add(new ChildAssocRef(n13, QName.createQName("namespace", "common"), n14));
            }
            return assocs;
            
        }

        public NodeRef getPrimaryParent(NodeRef nodeRef) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public void createAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname) throws InvalidNodeRefException, AssociationExistsException
        {
            throw new UnsupportedOperationException();
        }

        public Collection<NodeRef> getAssociationSources(NodeRef targetRef, QName qname) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Collection<NodeRef> getAssociationTargets(NodeRef sourceRef, QName qname) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName qname) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Path getPath(NodeRef nodeRef) throws InvalidNodeRefException
        {
            throw new UnsupportedOperationException();
        }

        public Collection<Path> getPaths(NodeRef nodeRef, boolean primaryOnly) throws InvalidNodeRefException
        {
            List<Path> paths = new ArrayList<Path>();
            if (nodeRef.getId().equals("0"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("1"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("2"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("3"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "three"), n3)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("4"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "four"), n4)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("5"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("6"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "six"), n6)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("7"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("namespace", "seven"), n7)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("8"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "eight-0"), n8)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "eight-1"), n8)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("namespace", "eight-2"), n8)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("9"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "nine"), n9)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("10"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "ten"), n10)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("11"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "eleven"), n11)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("12"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "twelve"), n12)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("13"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "twelve"), n12)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n12, QName.createQName("namespace", "thirteen"), n13)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("namespace", "link"), n13)));
                paths.add(path);
            }
            else if (nodeRef.getId().equals("14"))
            {
                Path path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "twelve"), n12)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n12, QName.createQName("namespace", "thirteen"), n13)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n13, QName.createQName("namespace", "fourteen"), n14)));
                paths.add(path);

                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "twelve"), n12)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n12, QName.createQName("namespace", "thirteen"), n13)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n13, QName.createQName("namespace", "common"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "twelve"), n12)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n12, QName.createQName("namespace", "common"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "five"), n5)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n5, QName.createQName("namespace", "common"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "six"), n6)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n6, QName.createQName("namespace", "common"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "one"), n1)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n1, QName.createQName("namespace", "common"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("namespace", "common"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("namespace", "link"), n13)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n13, QName.createQName("namespace", "fourteen"), n14)));
                paths.add(path);
                path = new Path();
                path.append(new Path.ChildAssocElement(new ChildAssocRef(null, null, rootNode)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(rootNode, QName.createQName("namespace", "two"), n2)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n2, QName.createQName("namespace", "link"), n13)));
                path.append(new Path.ChildAssocElement(new ChildAssocRef(n13, QName.createQName("namespace", "common"), n14)));
                paths.add(path);

            }

            return paths;
        }

    }

    private static class MockNameService implements NamespaceService
    {
        private static HashMap<String, String> map = new HashMap<String, String>();

        static
        {
            map.put(NamespaceService.ACTIVITI_PREFIX, NamespaceService.ACTIVITI_URI);
            map.put(NamespaceService.ACTIVITI_TEST_PREFIX, NamespaceService.ACTIVITI_TEST_URI);
            map.put(NamespaceService.DEFAULT_PREFIX, "namespace");
            map.put("namespace", "namespace");
            
        }
        
        public Collection<String> getURIs()
        {
            return map.values();
        }

        public Collection<String> getPrefixes()
        {
           return map.keySet();
        }

        public String getNamespaceURI(String prefix) throws NamespaceException
        {
            return map.get(prefix);
        }

        public Collection<String> getPrefixes(String namespaceURI) throws NamespaceException
        {
            HashSet<String> answer = new HashSet<String>();
            for(String prefix: map.keySet())
            {
                String test = map.get(prefix);
                if(test.equals(namespaceURI))
                {
                    answer.add(prefix);
                }
            }
            return answer;
        }
        
    }
    
    private class MockDictionaryService implements DictionaryService
    {

        public Collection<ClassRef> getTypes()
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public ClassDefinition getClass(ClassRef classRef)
        {
            if(classRef.equals(DictionaryBootstrap.TYPE_FILE))
            {
                return new MockClassDefinition(DictionaryBootstrap.TYPE_FILE);
            }
            else if(classRef.equals(DictionaryBootstrap.TYPE_FOLDER))
            {
                return new MockClassDefinition(DictionaryBootstrap.TYPE_FOLDER);
            }
            throw new UnsupportedOperationException();
        }

        public TypeDefinition getType(ClassRef typeRef)
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public AspectDefinition getAspect(ClassRef aspectRef)
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }

        public PropertyDefinition getProperty(PropertyRef propertyRef)
        {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException();
        }
        
    }
    
    private class MockClassDefinition implements ClassDefinition
    {
        ClassRef ref;
        
        MockClassDefinition(ClassRef ref)
        {
            this.ref = ref;
        }

        public ClassRef getReference()
        {
            throw new UnsupportedOperationException();
        }

        public QName getQName()
        {
            throw new UnsupportedOperationException();
        }

        public ClassDefinition getSuperClass()
        {
            throw new UnsupportedOperationException();
        }

        public ClassRef getBootstrapClass()
        {
           return ref;
        }

        public boolean isAspect()
        {
            throw new UnsupportedOperationException();
        }

        public AssociationDefinition getAssociation(String name)
        {
            throw new UnsupportedOperationException();
        }

        public List<AssociationDefinition> getAssociations()
        {
            throw new UnsupportedOperationException();
        }

        public List<PropertyDefinition> getProperties()
        {
            throw new UnsupportedOperationException();
        }

        public PropertyDefinition getProperty(String name)
        {
            throw new UnsupportedOperationException();
        }
    }
    
    public static void main(String[] args)
    {
        // String guid = GUID.generate();
        // System.out.println("GUID is " + guid + " length is " +
        // guid.length());
        LuceneTest test = new LuceneTest();
        test.testPathSearch();
        //test.testStandAloneIndexerCommit();
    }
}
