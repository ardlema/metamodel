/**
 * eobjects.org MetaModel
 * Copyright (C) 2010 eobjects.org
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.eobjects.metamodel.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base object type with conveniently implemented base methods like hashCode()
 * and equals(). Subclasses should implement the {@link #decorateIdentity(List)}
 * method to have {@link #equals(Object)} and {@link #hashCode()} automatically
 * implemented.
 * 
 * @author Kasper Sørensen
 */
public abstract class BaseObject {

	private static final Logger logger = LoggerFactory
			.getLogger(BaseObject.class);

	@Override
	public String toString() {
		// overridden version of toString() method that uses identity hash code
		// (to prevent hashCode() recursion due to logging!)
		return getClass().getName() + "@"
				+ Integer.toHexString(System.identityHashCode(this));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final int hashCode() {
		logger.debug("{}.hashCode()", this);
		int hashCode = -1;
		List<Object> list = new ArrayList<Object>();
		decorateIdentity(list);
		if (list.isEmpty()) {
			list.add(toString());
		}
		hashCode -= list.size();
		for (Object obj : list) {
			hashCode += hashCode(obj);
		}
		return hashCode;
	}

	private static final int hashCode(Object obj) {
		if (obj == null) {
			logger.debug("obj is null, returning constant");
			return -17;
		}
		if (obj.getClass().isArray()) {
			logger.debug("obj is an array, returning a sum");
			int length = Array.getLength(obj);
			int hashCode = 4324;
			for (int i = 0; i < length; i++) {
				Object o = Array.get(obj, i);
				hashCode += hashCode(o);
			}
			return hashCode;
		}
		logger.debug("obj is a regular object, returning hashCode");
		return obj.hashCode();
	}

	/**
	 * Override this method if the equals method should support different
	 * subtypes. For example, if different subtypes of Number should be
	 * supported, implement this method with:
	 * 
	 * <code>
	 * obj instanceof Number
	 * </code>
	 * 
	 * and make sure that the decorateIdentity(...) method will always return a
	 * comparable list of identity-objects.
	 * 
	 * @param obj
	 * @return true if the provided object's class is accepted for equals
	 *         comparison
	 */
	protected boolean classEquals(BaseObject obj) {
		return getClass() == obj.getClass();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj instanceof BaseObject) {
			BaseObject that = (BaseObject) obj;
			if (classEquals(that)) {
				List<Object> list1 = new ArrayList<Object>();
				List<Object> list2 = new ArrayList<Object>();

				decorateIdentity(list1);
				that.decorateIdentity(list2);

				if (list1.size() != list2.size()) {
					throw new IllegalStateException(
							"Two instances of the same class ("
									+ getClass().getName()
									+ ") returned different size decorated identity lists");
				}

				if (list1.isEmpty()) {
					assert list2.isEmpty();
					list1.add(toString());
					list2.add(that.toString());
				}

				EqualsBuilder eb = new EqualsBuilder();

				Iterator<Object> it1 = list1.iterator();
				Iterator<Object> it2 = list2.iterator();
				while (it1.hasNext()) {
					assert it2.hasNext();
					Object next1 = it1.next();
					Object next2 = it2.next();
					eb.append(next1, next2);
				}
				assert !it2.hasNext();

				return eb.isEquals();
			}
		}
		return false;
	}

	/**
	 * Subclasses should implement this method and add all fields to the list
	 * that are to be included in equals(...) and hashCode() evaluation
	 * 
	 * @param identifiers
	 */
	protected abstract void decorateIdentity(List<Object> identifiers);
}
