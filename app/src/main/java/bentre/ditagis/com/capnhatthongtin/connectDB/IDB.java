package bentre.ditagis.com.capnhatthongtin.connectDB;

import java.util.List;

public interface IDB<E, T, K> {
    /*
     * add
     */
    public T add(E e);

    public T delete(K k);

    public T update(E e);

    public E find(K k, K k1);

    public E find(K k, K k1, K k2);

    public List<E> getAll();
}

