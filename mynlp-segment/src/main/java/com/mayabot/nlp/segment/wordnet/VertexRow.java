/*
 *  Copyright 2017 mayabot.com authors. All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.mayabot.nlp.segment.wordnet;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class VertexRow implements Iterable<Vertex> {

    private Vertex first;

    // 暂定和sindex一样，-1表示#start
    int rowNum;

    Wordnet wordnet;

    //private Vertex select; //Path 路径选择指针

    private int size;


    VertexRow(int rowNum, Wordnet wordnet) {
        this.rowNum = rowNum;
        this.wordnet = wordnet;
    }

    public Vertex getOrCrete(short length) {

        Vertex v = get(length);

        if (v == null) {
            v = new Vertex(length);
            this.put(v);
        }

        return v;
    }

    public void clear() {
        this.size = 0;
        //select = null;
        first = null;
    }

    /**
     * @param length
     * @return may be null if not exits
     */
    public Vertex get(int length) {
        //优化空
        if (first == null) {
            return null;
        }

        //优化1个元素的情况
        if (size == 1) {
            if (first.length == length) {
                return first;
            } else {
                return null;
            }
        }

        //优化2个元素的情况
        if (size == 2) {
            if (first.length == length) {
                return first;
            } else if (first.next.length == length) {
                return first.next;
            } else {
                return null;
            }
        }

        //优化3个元素的情况
        if (size == 3) {
            if (first.length == length) {
                return first;
            } else if (first.next.length == length) {
                return first.next;
            } else if (first.next.next.length == length) {
                return first.next.next;
            } else {
                return null;
            }
        }

        //更多 使用循环
        for (Vertex x = first; x != null; x = x.next) {
            if (x.length == length) {
                return x;
            }

            //提高检索性能
            Vertex _next = x.next;
            if (_next != null) {
                if (length == _next.length) {
                    return _next;
                } else if (length < _next.length) {
                    return null;
                }
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * 移除长度为length的节点。成功就返回被删除的节点
     *
     * @param length
     * @return
     */
    public Vertex remove(short length) {
        Vertex v = get(length);
        if (v != null) {
            unlink(v);
            //this.wordnet.subtractionFlag(this.rowNum+v.length); //给被链入的行记数字
//			if(v == select){
//				select = null;
//			}
        }

        return v;
    }

    /**
     * 设置 vertext 以 length为key ,如果替换了返回被替换者
     *
     * @param v
     * @return
     */
    public Vertex put(Vertex v) {

        //如果当期是优化网络模式，那么节点也是优化网络的节点模式
        if (this.wordnet.isOptimizeNet()) {
            v.setOptimize(true);
            v.setOptimizeNewNode(true);
        }

        if (v.length != 0) { //END节点的length就是0
            //this.wordnet.addFlag(this.rowNum+v.length); //给被链入的行记数字
        }

        int key = v.length;
        //两种操作要么，要么替换、要么插入
        v.vertexRow = this;
        v.next = null;
        v.prev = null;

//		if(v.length==3 && v.realWord().equals("大观园")){
//			System.out.println();
//		}

        if (isEmpty()) {
            linkFirst(v);
            return null;
        }

        Vertex point = null;
        for (Vertex x = first; x != null; x = x.next) {
            point = x;
            if (key == x.length) {
                //替换吧
                replace(x, v);

//				if(x == select){ //选择这个新的节点
//					select = v;
//				}
                return x;

            }
            if (x.next == null) {
                if (key < x.length) {
                    //查到前面
                    linkBefore(v, x);
                    return null;
                } else {
                    linkAfter(v, x);
                    return null;
                }
            } else {
                if (key < x.length) {
                    linkBefore(v, x);
                    return null;
                } else { // key > x.length
                    //再分两种情况
                    if (key < x.next.length) {
                        linkBefore(v, x.next);
                        return null;
                    } else {
                    }
                }

            }
        }
        linkAfter(v, point);
        return null;
    }

    public boolean isEmpty() {
        return first == null;
    }

    public boolean contains(int len) {
        return get(len) != null;
    }

    public int size() {
        return size;
    }

    public Set<Integer> keys() {
        Set<Integer> set = Sets.newTreeSet();
        for (Vertex x = first; x != null; x = x.next) {
            set.add(x.length);
        }
        return set;
    }


    /**
     * Unlinks non-null node x.
     */
    private Vertex unlink(Vertex x) {
        // assert x != null;
        final Vertex next = x.next;
        final Vertex prev = x.prev;

        if (prev == null) {
            first = next;
        } else {
            prev.next = next;
            x.prev = null;
        }

        if (next == null) {
            // last = prev;
        } else {
            next.prev = prev;
            x.next = null;
        }

        size--;

        return x;
    }

    private void replace(Vertex old, Vertex replaced) {
        final Vertex prev = old.prev;
        final Vertex next = old.next;
        old.prev = null;
        old.next = null;

        replaced.prev = prev;
        replaced.next = next;

        if (prev != null) {
            prev.next = replaced;
        }

        if (next != null) {
            next.prev = replaced;
        }

        if (first == old) {
            first = replaced;
        }

//		if(old==select){
//			this.select = replaced;
//		}
    }

    /**
     * Links e as first element.
     */
    private void linkFirst(Vertex newNode) {
        newNode.next = null;
        newNode.prev = null;

        final Vertex f = first;
        newNode.next = f;

        first = newNode;

        if (f == null) {
            // last = newNode;
        } else {
            f.prev = newNode;
        }
        size++;
    }

    private void linkAfter(Vertex newNode, Vertex succ) {
        // assert succ != null;
        final Vertex nexted = succ.next;

        newNode.prev = succ;
        newNode.next = nexted;

        succ.next = newNode;

        if (nexted != null) {
            nexted.prev = newNode;
        }

        size++;
    }

    /**
     * Inserts element e before non-null Node succ.
     */
    private void linkBefore(Vertex newNode, Vertex succ) {
        // assert succ != null;
        final Vertex pred = succ.prev;
        newNode.prev = pred;
        newNode.next = succ;
        succ.prev = newNode;

        if (pred == null)
            first = newNode;
        else
            pred.next = newNode;
        size++;
    }

    @Override
    public String toString() {
        return "Slot[" + rowNum + "]=>" + this.keys();
    }

    @Override
    public Iterator<Vertex> iterator() {
        return new AbstractIterator<Vertex>() {
            Vertex point = first;

            @Override
            protected Vertex computeNext() {
                if (point == null) {
                    return endOfData();
                } else {
                    Vertex old = point;
                    point = old.next;
                    return old;
                }
            }
        };
    }

    public List<Vertex> values() {
        return Lists.newArrayList(this);
    }

    public Vertex getFirst() {
        return first;
    }

    public Vertex first() {
        return first;
    }

//	public Vertex getSelect() {
//		return select;
//	}
//
//	void selectVertex(Vertex select) {
//		this.select = select;
//	}

    public char theChar() {
        return wordnet.charAt(rowNum);
    }

    public int getRowNum() {
        return rowNum;
    }

    public boolean isNotEmtpy() {
        return first != null;
    }
}
