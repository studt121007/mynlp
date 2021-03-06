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

package com.mayabot.nlp.segment;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.io.Reader;
import java.io.StringReader;
import java.util.List;

/**
 * 一个分词器。
 * 不能被多线程访问，它是有状态的.
 * 通过调用reset方法，可以重复使用对象
 *
 * @author jimichan
 */
public interface MynlpSegment extends Iterable<MynlpTerm> {

    MynlpTerm next();

    MynlpSegment reset(Reader reader);

    default MynlpSegment reset(String text) {
        return this.reset(new StringReader(text));
    }


    /**
     * 一个便捷的方法，获得分词结果
     *
     * @return
     */
    default List<String> toWords() {
        return Lists.newArrayList(Iterators.transform(iterator(), MynlpTerm::getWord));
    }

}
