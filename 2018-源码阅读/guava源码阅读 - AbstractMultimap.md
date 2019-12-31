#### guava源码阅读 - AbstractMultimap

AbstractMultimap是对Multimap接口的一个骨架实现,下面是我在读源码时对每一段代码的分析评论：

```
@GwtCompatible
abstract class AbstractMultimap<K, V> implements Multimap<K, V> {
  //判空
  @Override
  public boolean isEmpty() {
    return size() == 0;
  }
  //遍历每个Map<K, Collection<V>>视图的每一个集合，如果包含这个value返回true
  @Override
  public boolean containsValue(@Nullable Object value) {
    for (Collection<V> collection : asMap().values()) {
      if (collection.contains(value)) {
        return true;
      }
    }

    return false;
  }
  //是否包含这个键值对Entry
  @Override
  public boolean containsEntry(@Nullable Object key, @Nullable Object value) {
    Collection<V> collection = asMap().get(key);
    //使用三元运算符，如果这个key对于的集合为null直接就表示不包含这个Entry了
    return collection != null && collection.contains(value);
  }
  //移除一个元素，注意asMap()视图删除元素对multimap有影响，但不能通过这个视图添加元素
  @CanIgnoreReturnValue
  @Override
  public boolean remove(@Nullable Object key, @Nullable Object value) {
    Collection<V> collection = asMap().get(key);
    return collection != null && collection.remove(value);
  }
  //multimap添加一个键值对；直接调用get()方法会返回一个空的collection，但调用asMap()方法得到一个map视图再调用get（key）如果key不存在会返回null
  @CanIgnoreReturnValue
  @Override
  public boolean put(@Nullable K key, @Nullable V value) {
    return get(key).add(value);
  }
  
  //针对一个键，添加对应这个键的一个可迭代集合
  @CanIgnoreReturnValue
  @Override
  public boolean putAll(@Nullable K key, Iterable<? extends V> values) {
    checkNotNull(values);
    // make sure we only call values.iterator() once
    // and we only call get(key) if values is nonempty
    if (values instanceof Collection) {
      Collection<? extends V> valueCollection = (Collection<? extends V>) values;
      //addAll方法可以往collection中添加一个collection
      return !valueCollection.isEmpty() && get(key).addAll(valueCollection);
    } else {
      Iterator<? extends V> valueItr = values.iterator();
      //guava的工具类Iterators.addAll()往一个集合中添加所有iterator中的元素
      return valueItr.hasNext() && Iterators.addAll(get(key), valueItr);
    }
  }

  @CanIgnoreReturnValue
  @Override
  public boolean putAll(Multimap<? extends K, ? extends V> multimap) {
    boolean changed = false;
    for (Entry<? extends K, ? extends V> entry : multimap.entries()) {
      changed |= put(entry.getKey(), entry.getValue());
    }
    return changed;
  }

  @CanIgnoreReturnValue
  @Override
  public Collection<V> replaceValues(@Nullable K key, Iterable<? extends V> values) {
    checkNotNull(values);
    Collection<V> result = removeAll(key);
    putAll(key, values);
    return result;
  }

  private transient @MonotonicNonNull Collection<Entry<K, V>> entries;

  @Override
  public Collection<Entry<K, V>> entries() {
    Collection<Entry<K, V>> result = entries;
    return (result == null) ? entries = createEntries() : result;
  }

  abstract Collection<Entry<K, V>> createEntries();

  @WeakOuter
  class Entries extends Multimaps.Entries<K, V> {
    @Override
    Multimap<K, V> multimap() {
      return AbstractMultimap.this;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
      return entryIterator();
    }

    @Override
    public Spliterator<Entry<K, V>> spliterator() {
      return entrySpliterator();
    }
  }

  @WeakOuter
  class EntrySet extends Entries implements Set<Entry<K, V>> {
    @Override
    public int hashCode() {
      return Sets.hashCodeImpl(this);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
      return Sets.equalsImpl(this, obj);
    }
  }

  abstract Iterator<Entry<K, V>> entryIterator();

  Spliterator<Entry<K, V>> entrySpliterator() {
    return Spliterators.spliterator(
        entryIterator(), size(), (this instanceof SetMultimap) ? Spliterator.DISTINCT : 0);
  }

  private transient @MonotonicNonNull Set<K> keySet;

  @Override
  public Set<K> keySet() {
    Set<K> result = keySet;
    return (result == null) ? keySet = createKeySet() : result;
  }

  abstract Set<K> createKeySet();

  private transient @MonotonicNonNull Multiset<K> keys;

  @Override
  public Multiset<K> keys() {
    Multiset<K> result = keys;
    return (result == null) ? keys = createKeys() : result;
  }

  abstract Multiset<K> createKeys();

  private transient @MonotonicNonNull Collection<V> values;

  @Override
  public Collection<V> values() {
    Collection<V> result = values;
    return (result == null) ? values = createValues() : result;
  }

  abstract Collection<V> createValues();

  @WeakOuter
  class Values extends AbstractCollection<V> {
    @Override
    public Iterator<V> iterator() {
      return valueIterator();
    }

    @Override
    public Spliterator<V> spliterator() {
      return valueSpliterator();
    }

    @Override
    public int size() {
      return AbstractMultimap.this.size();
    }

    @Override
    public boolean contains(@Nullable Object o) {
      return AbstractMultimap.this.containsValue(o);
    }

    @Override
    public void clear() {
      AbstractMultimap.this.clear();
    }
  }

  Iterator<V> valueIterator() {
    return Maps.valueIterator(entries().iterator());
  }

  Spliterator<V> valueSpliterator() {
    return Spliterators.spliterator(valueIterator(), size(), 0);
  }

  private transient @MonotonicNonNull Map<K, Collection<V>> asMap;

  @Override
  public Map<K, Collection<V>> asMap() {
    Map<K, Collection<V>> result = asMap;
    return (result == null) ? asMap = createAsMap() : result;
  }

  abstract Map<K, Collection<V>> createAsMap();

  // Comparison and hashing

  @Override
  public boolean equals(@Nullable Object object) {
    return Multimaps.equalsImpl(this, object);
  }

  /**
   * Returns the hash code for this multimap.
   *
   * <p>The hash code of a multimap is defined as the hash code of the map view, as returned by
   * {@link Multimap#asMap}.
   *
   * @see Map#hashCode
   */
  @Override
  public int hashCode() {
    return asMap().hashCode();
  }

  /**
   * Returns a string representation of the multimap, generated by calling {@code toString} on the
   * map returned by {@link Multimap#asMap}.
   *
   * @return a string representation of the multimap
   */
  @Override
  public String toString() {
    return asMap().toString();
  }
}
```