/**
 * Copyright (C) 2015 Oneki (http://www.oneki.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.oneki.plugin.utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class EnumerationList<E> implements Enumeration<E> {

	private List<E> elements = new ArrayList<E>();
	private Iterator<E> it;
	
	public void addElement(E element) {
		elements.add(element);
	}
	
	public void addElement(Enumeration<E> elements) {
		if(elements != null) {
			while(elements.hasMoreElements()) {
				addElement(elements.nextElement());
			}
		}
	}
	
	@Override
	public boolean hasMoreElements() {
		if(it == null) {
			it = elements.iterator();
		}
		return it.hasNext();
	}

	@Override
	public E nextElement() {
		if(it == null) {
			it = elements.iterator();
		}
		return it.next();
	}
	
}