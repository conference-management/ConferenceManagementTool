package utils;

import java.util.List;

public class LexicographicalComparator implements java.util.Comparator<Pair<java.util.List<Integer>, String>> {

    @Override
    public int compare(Pair<List<Integer>, String> o1, Pair<List<Integer>, String> o2) {
        int iteration = 0;
        while(true) {
            if(o1.first().size() == iteration && o2.first().size() == iteration) {
                return 0;
            } else if(o1.first().size() == iteration) {
                return -1;
            } else if(o2.first().size() == iteration) {
                return 1;
            } else if(!o1.first().get(iteration).equals(o2.first().get(iteration))) {
                return Integer.compare(o1.first().get(iteration), o2.first().get(iteration));
            }
            iteration++;
        }
    }
}
