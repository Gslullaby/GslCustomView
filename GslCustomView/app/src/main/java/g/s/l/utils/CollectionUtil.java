package g.s.l.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Deemo on 16/9/6.
 * (～ o ～)~zZ
 */
public class CollectionUtil {

    public static boolean isNullOrEmptyList(List list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNullOrEmptySet(Set set) {
        return set == null || set.isEmpty();
    }

    public static boolean isNullOrEmptyMap(Map map) {
        return map == null || map.isEmpty();
    }

}
