// Deshawn Brooks
// cop 3502 Summer 2019 Dr. Szumlanski
// skipList assignment 4

import java.util.*;
import java.lang.Math;

class Node <Type extends Comparable<Type>>
{
    HashMap<Integer, Node<Type>> next = new HashMap<>();
    Type data;
    int height;
    
    Node(Type data, int height)
    {
        this.data = data;
        this.height = height;
        for(int i = 0; i < height; i++)
        {
            this.next.put(i, null);
        }
    }

    // Returns the node being pointed at, at this height
    public Node<Type> next(int height)
    {
        if(height < this.height && height >= 0)
        {
            return next.get(height);
        }
        else
        {
            return null;
        }
    }

    // points to passed in node at this height. Also checks if node
    // can be placed at specified height.
    public void setPointer(int height, Node<Type> node)
    {
        if(height <= this.height && height >= 0)
        {
            next.put(height, node);
        }
    }

    // Grows node by one
    public void grow()
    {
        next.put(this.height, null);
        this.height++;
    }

    // Trims node to height that you specify
    public void trim(int height)
    {
        for(int i = this.height - 1; i >= height; i--)
        {
            this.next.remove(i);
        }
        this.height = height;
    }

    // O(1) method that returns value in node
    public Type value()
    {
        return this.data;
    }

    // O(1) method that returns current height of node
    public int height()
    {
        return this.height;
    }
}

public class SkipList <Type extends Comparable<Type>>
{
    private Node<Type> head = null;
    private int nodeCount = 0;

    // This Simply creates a skipList with height of 1
    public SkipList()
    {
        insert(null, 1);
    }

    // This creates a skipList with a specified height
    public SkipList(int height)
    {
        insert(null, height);
    }

    // Insert data into skiplist, duplicates should be placed directly in front
    // of original, generate random height, increase height if node cause Log increase.
    // This function works hand in hand with second insert because this insert contains no search method.
    public void insert(Type data)
    {
        // If head node is only node in Skiplist insert node right after head
        if(head.next(0) == null)
        {
            int height = 1;
            insert(data, height);
        }
        else
        {
            Node<Type> tempNode = head;
            int heightCheck, height, i = 0;

            height = tempNode.height;
            heightCheck = getMaxHight(size() + 1);

            // Check if insertion causes increase in height. if so grow height size
            if(height < heightCheck)
            {
                HashMap<Integer, Node<Type>> list = new HashMap<>();
                head.grow();
                height = heightCheck;
                tempNode = tempNode.next(height - 1);

                while(tempNode != null)
                {
                    randomGrow(tempNode);
                    if(tempNode.height() == head.height)
                    {
                        list.put(i, tempNode);
                        i++;
                    }

                    tempNode = tempNode.next(height - 2);
                }

                tempNode = head;

                for(i = 0; i < list.size(); i++)
                {
                    tempNode.setPointer(head.height, list.get(i));
                    tempNode = tempNode.next(head.height);
                }
            }
            
            height = getRandomHeight(size() + 1);

            // Pass data and height to second method
            insert(data, height);
        }

    }

    // Same as last but use height passed into method
    public void insert(Type data, int height)
    {
        // If inserting head into list
        if(head() == null && data == null)
            head = new Node<>(data, height);

        // If inserting regular node into list, assuming you don't pass in null data
        if(head() != null && data != null)
        {
            Node<Type> node = new Node<>(data, height), tempNode = head;
            HashMap<Integer, Node<Type>> nodeTracker = new HashMap<>();
            int tempHeight = tempNode.height - 1, i = 0;
            Type tempData;

            // If head node is only node in Skiplist insert node right after head
            if(head.next(0) == null)
            {
                for(i = 0; i < height; i++)
                {
                    head.setPointer(i, node);
                }
                nodeCount++;
            }
            else
            {
                // Main search method that keeps track of next pointers
                while(tempHeight >= 0)
                {
                    // First check if next pointer at this height points null, if so store next pointer then drop down.
                    if(tempNode.next(tempHeight) != null)
                    {
                        tempData = tempNode.next(tempHeight).data;

                        // If data is less than data pointed at, keep track of next pointer then drop down
                        // If data is greater than pointed at data, skip to that node
                        if (data.compareTo(tempData) <= 0) 
                        {
                            // We only care about tracking nodes that are at most as tall as the node being inserted
                            if (tempHeight <= height - 1)
                            {
                                nodeTracker.put(tempHeight, tempNode);
                            }
                            tempHeight--;
                        } 
                        else 
                        {
                            tempNode = tempNode.next(tempHeight);
                            tempData = tempNode.data;
                        }
                    }
                    else
                    {
                        if (tempHeight <= height - 1 && tempHeight >= 0)
                        {
                            nodeTracker.put(tempHeight, tempNode);
                        }
                        tempHeight--;
                    }
                }

                // Assigning the next pointers based on the nodes we tracked
                tempHeight = node.height - 1;
                for(i = tempHeight; i >= 0; i--)
                {
                    node.setPointer(i, nodeTracker.get(i).next(i));
                    nodeTracker.get(i).setPointer(i, node);
                }

                // Increment node counter
                nodeCount++;
            }
        }
    }


    // Delete single instance of node in skipList. If there is a duplicate, Delete
    // first (Leftmost) instance only. If max height must fall trim nodes
    // to new lower height.
    public void delete(Type data)
    {
        Node<Type> tempNode = head, node = null;
        int tempHeight = tempNode.height - 1, heightCheck, i;
        Type tempData;

        HashMap<Integer, Node<Type>> nodeTracker = new HashMap<>();

        // Similiar to "insert" search method except we keep track of nodes pointing
        // at node to be deleted
        while(tempHeight >= 0)
        {
            if(tempNode.next(tempHeight) != null)
            {
                tempData = tempNode.next(tempHeight).data;

                // If we found a hit then keep track of node pointing to "node to be deleted" at this height
                // then drop down. (We account for duplicates in "isEmpty" if statment.
                if(data.compareTo(tempData) == 0)
                {
                    nodeTracker.put(tempHeight, tempNode);
                    tempHeight--;
                }
                else
                {
                    if(data.compareTo(tempData) < 0)
                    {
                        tempHeight--;
                    }
                    else
                    {
                        tempNode = tempNode.next(tempHeight);
                        tempData = tempNode.data;
                    }
                }
            }
            else
            {
                tempHeight--;
            }
        }

        // If data is not in skiplist then nodes will point to the data in skiplist which leaves the array empty,
        // So we check array length.
        if(!nodeTracker.isEmpty())
        {
            // To delete the first instance of a node we start by getting node pointed at from Height (1 aka 0).
            // then we only consider nodes in nodetracker <= (height of that node). We then move around the
            // pointers essentially getting rid of only that node.
            node = nodeTracker.get(0).next(0);

            for(i = 0; i < nodeTracker.size(); i++)
            {
                tempNode = nodeTracker.get(i);

                if(i > node.height - 1)
                {
                    break;
                }
                else if(tempNode != null)
                {
                    tempNode.setPointer(i, node.next(i));
                }
            }

            // Decrement node counter
            nodeCount--;

            // Check if deletion causes max height to shrink, if so shrink nodes to appropriate height
            heightCheck = getMaxHight(size());

            // If no nodes are in the skiplist, revert back to empty state with a height of one
            if(heightCheck == 0 && size() == 0)
            {
                head.trim(1);
            }
            else if(heightCheck == 0 && size() == 1)
            {
                // If we are left with one node in skiplist then do nothing
            }
            else if(height() > heightCheck)
            {
                tempNode = head;

                while(tempNode != null)
                {
                    tempNode.trim(heightCheck);
                    tempNode = tempNode.next(heightCheck - 1);
                }
            }
        }
    }

    // O(1) method that returns current maxHeight of node
    public int height()
    {
        return head.height;
    }

    // Return first reference to node in skipList with this value. Otherwise return Null
    public Node<Type> get(Type data)
    {
        Node<Type> tempNode = head, foundNode = null;
        int tempHeight = tempNode.height - 1;
        Type tempData;

        // Similar to insert search but only tracking one node
        while(tempHeight >= 0)
        {
            if(tempNode.next(tempHeight) != null)
            {
                tempData = tempNode.next(tempHeight).data;
                // If we found a hit then keep track of the node and drop down. We only want to keep first reference of
                // that data so we keep searching just in case there is duplicate placed before this node.
                if(data.compareTo(tempData) == 0)
                {
                    foundNode = tempNode.next(tempHeight);
                    tempHeight--;
                }
                else
                {
                    if(data.compareTo(tempData) < 0)
                    {
                        tempHeight--;
                    }
                    else
                    {
                        tempNode = tempNode.next(tempHeight);
                        tempData = tempNode.data;
                    }
                }
            }
            else
            {
                tempHeight--;
            }
        }

        if(foundNode == null)
        {
            return null;
        }
        else
        {
            return foundNode;
        }
    }

    // Check if list contains data and return true or false must be search O(Log_n)
    public boolean contains(Type data)
    {
        Node<Type> tempNode = head;
        int tempHeight = tempNode.height - 1;
        Type tempData;

        // Similar to insert search method without the tracking
        while(tempHeight >= 0)
        {
            if(tempNode.next(tempHeight) != null)
            {
                tempData = tempNode.next(tempHeight).data;

                // If we found a hit then return true in this case.
                if(data.compareTo(tempData) == 0)
                {
                    return true;
                }
                else
                {
                    if(data.compareTo(tempData) < 0)
                    {
                        tempHeight--;
                    }
                    else
                    {
                        tempNode = tempNode.next(tempHeight);
                        tempData = tempNode.data;
                    }
                }
            }
            else
            {
                tempHeight--;
            }
        }
        return false;
    }

    // O(1) method that returns number of nodes (Exclude head)
    public int size()
    {
        return nodeCount;
    }

    // Returns head of skipList
    public Node<Type> head()
    {
        return head;
    }

    // Returns max height of skip list with "N" Nodes
    private static int getMaxHight(double n)
    {
        // Here we check for the case of log(0) and just return 1 in that case
        int i = (int) Math.ceil(Math.log(n) / Math.log(2.0));
        if(i < 0)
        {
            return 0;
        }
        else
        {
            return i;
        }
    }

    // Essentially keep flipping a coin with a constraint of
    // numbers > 1 or numbers <= maximally tall number
    private static int getRandomHeight(int n)
    {
        int i = 0, max = getMaxHight(n);

        while(i < max)
        {
            if (Math.random() < 0.50)
            {
                i++;
            }
            else
            {
                if(i > 0)
                {
                    break;
                }
            }
        }
        return i;
    }

    // Grow this node by one with a probablity of 50%
    private void randomGrow(Node<Type> node)
    {
        if (Math.random() < 0.50)
        {
            node.grow();
        }
    }
}