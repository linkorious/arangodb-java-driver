/*
 * DISCLAIMER
 *
 * Copyright 2016 ArangoDB GmbH, Cologne, Germany
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright holder is ArangoDB GmbH, Cologne, Germany
 */

package com.arangodb.internal;

import java.util.Iterator;
import java.util.NoSuchElementException;

import com.arangodb.ArangoCursor;
import com.arangodb.entity.CursorEntity;

/**
 * @author Mark - mark at arangodb.com
 * @param <T>
 *
 */
public class ArangoCursorIterator<T> implements Iterator<T> {

	private CursorEntity result;
	private int pos;

	private final ArangoCursor<T> cursor;
	private final InternalArangoDatabase<?, ?, ?> db;
	private final ArangoCursorExecute execute;

	public ArangoCursorIterator(final ArangoCursor<T> cursor, final ArangoCursorExecute execute,
		final InternalArangoDatabase<?, ?, ?> db, final CursorEntity result) {
		super();
		this.cursor = cursor;
		this.execute = execute;
		this.db = db;
		this.result = result;
		pos = 0;
	}

	@Override
	public boolean hasNext() {
		return pos < result.getResult().size() || result.getHasMore();
	}

	@Override
	public T next() {
		if (pos >= result.getResult().size() && result.getHasMore()) {
			result = execute.next(cursor.getId());
			pos = 0;
		}
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		return db.executor.deserialize(result.getResult().get(pos++), cursor.getType());
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
