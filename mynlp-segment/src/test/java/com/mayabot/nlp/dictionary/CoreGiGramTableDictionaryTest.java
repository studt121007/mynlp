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

package com.mayabot.nlp.dictionary;

import com.mayabot.nlp.BaseTest;
import com.mayabot.nlp.MyNlps;
import com.mayabot.nlp.segment.dictionary.core.CoreBiGramTableDictionary;
import org.junit.Test;

public class CoreGiGramTableDictionaryTest extends BaseTest {

    @Test
    public void test() {
        CoreBiGramTableDictionary dic = MyNlps.getInjector().getInstance(CoreBiGramTableDictionary.class);
    }

}
