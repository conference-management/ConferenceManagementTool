package agenda;

import com.google.gson.annotations.Expose;
import utils.LexicographicalComparator;
import utils.Pair;
import utils.WriterBiasedRWLock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Agenda implements AgendaObservable {

    @Expose
    protected List<Topic> topics;
    protected WriterBiasedRWLock lock;
    private Agenda parent;
    private ConcurrentHashMap<AgendaObserver, Boolean> observers = new ConcurrentHashMap<>(); // a map backed hashset

    /**
     * @param agenda String that got extracted from a CSV file, please parse topics using this
     */
    public Agenda(String agenda) {
        this(convertStringToTopicList(agenda));
    }

    protected Agenda(Agenda parent, WriterBiasedRWLock lock) {
        this();
        this.lock = lock;
        this.parent = parent;
    }

    public Agenda() {
        this.topics = new LinkedList<>();
        this.lock = new WriterBiasedRWLock();
    }

    /**
     * Construct an Agenda with Preorder and Name of Topics.
     *
     * @param tops Preorder + Name list
     */
    public Agenda(List<Pair<List<Integer>, String>> tops) {
        this();
        Agenda ag = this;
        AtomicInteger depth = new AtomicInteger(1);
        while(tops.stream().anyMatch(p -> p.first().size() == depth.get())) {
            var ref = new Object() {
                Agenda aux = ag;
            };
            AtomicInteger auxDepth = new AtomicInteger(1);
            tops.stream().filter(p -> p.first().size() == depth.get()).sorted(new LexicographicalComparator())
                    .forEach(p -> {
                        auxDepth.set(1);
                        ref.aux = ag;
                        p.first().forEach(i -> {
                            i--;
                            if(auxDepth.get() < depth.get()) {
                                ref.aux = ref.aux.getTopic(i).getSubTopics();
                            } else {
                                ref.aux.addTopic(new Topic(p.second(), ref.aux), i);
                            }
                            auxDepth.getAndIncrement();
                        });
                    });
            depth.incrementAndGet();
        }
    }

    /**
     * Convert a CSV String into an TopicList.
     *
     * @param agenda
     *
     * @return
     */
    private static List<Pair<List<Integer>, String>> convertStringToTopicList(String agenda) {
        List<String> lines
                = Arrays.stream(agenda.trim().split("\\r?\\n")).map(String::trim).collect(Collectors.toList());
        List<List<String>> splitLines = new ArrayList<>();
        for(String s : lines) {
            splitLines.add(Arrays.asList(s.split("\\s+", 2)));
        }

        boolean format1 = true;
        for(List<String> l : splitLines) {
            if(l.size() != 2) {
                format1 = false;
                break;
            }
        }
        if(!format1) {
            throw new IllegalArgumentException("String format incorrect.");
        }

        List<Pair<List<Integer>, String>> tops = new ArrayList<>();
        try {
            for(List<String> l : splitLines) {
                tops.add(new Pair<>(Arrays.stream(l.get(0).split("\\.+"))
                        .map(Integer::parseInt).collect(Collectors.toList())
                        , l.get(1)));
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("String format incorrect.");
        }
        return tops;
    }

    /**
     * Remove Topic from actual Agenda.
     *
     * @param t Topic
     *
     * @return true iff removing was successful
     */
    boolean removeTopic(Topic t) {
        try {
            lock.getWriteAccess();
            return this.topics.remove(t);
        } catch (InterruptedException e) {
            return false;
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }

    /**
     * ReOrder Topic in Agenda.
     *
     * @param t   Topic
     * @param pos Position
     *
     * @return true iff reorder is successful
     */
    @Deprecated
    boolean reOrderTopic(Topic t, int pos) {
        return t.moveToNewAgenda(this, pos);
    }

    /**
     * Add Topic to the existing Agenda at Position pos.
     *
     * @param t   Topic
     * @param pos Position
     *
     * @return true iff adding was successful
     */
    public boolean addTopic(Topic t, int pos) {
        try {
            lock.getWriteAccess();
            if(pos >= 0 && pos <= topics.size()) {
                this.topics.add(pos, t);
                return true;
            }
            return false;
        } catch (InterruptedException e) {
            //do nothing
            return false;
        } finally {
            notifyObservers();
            lock.finishWrite();
        }
    }

    /**
     * Get specific Topic at Position pos.
     *
     * @param pos Position
     *
     * @return Topic
     */
    public Topic getTopic(int pos) {
        try {
            lock.getReadAccess();
            if(pos < 0 || pos >= topics.size()) {
                throw new IllegalArgumentException("Invalid position! Was: " + pos);
            }

            return this.topics.get(pos);
        } catch (InterruptedException e) {
            // do nothing
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Calculate PreOrder String List from actual Agenda.
     *
     * @return PreOrder String List
     */
    public List<String> preOrder() {
        try {
            lock.getReadAccess();
            List<String> strings = new LinkedList<>();
            for(int i = 0; i < this.getNumberOfTopics(); i++) {
                strings.add("" + (i + 1));
                for(String s : this.topics.get(i).getSubTopics().preOrder()) {
                    strings.add((i + 1) + "." + s);
                }
            }
            return strings;
        } catch (InterruptedException ignored) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Calculate the Number of Topics inside the Agenda.
     *
     * @return Number of Topics
     */
    public int getNumberOfTopics() {
        try {
            lock.getReadAccess();
            return topics.size();
        } catch (InterruptedException e) {
            return -1;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Get Topic from PreoderString
     *
     * @param preorder preorder String
     *
     * @return Topic
     */
    public Topic getTopicFromPreorderString(String preorder) {
        List<Integer> preorderList = getPreorderListFromPreorderString(preorder);
        try {
            lock.getReadAccess();
            return getTopicFromPreorderList(preorderList);
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Convert a PreOrderString to a PreOrder IntegerList.
     *
     * @param preorder String List
     *
     * @return Integer List
     */
    public List<Integer> getPreorderListFromPreorderString(String preorder) {
        String[] preorderArray = preorder.split("\\.");
        List<Integer> preorderList = new LinkedList<>();
        for(String s : preorderArray) {
            //here we convert counting from 1 to counting from 0
            preorderList.add((Integer.parseInt(s) - 1));
        }
        return preorderList;
    }

    /**
     * Create Topic from preoder List
     *
     * @param preorder List of Integer
     *
     * @return Topic
     */
    protected Topic getTopicFromPreorderList(List<Integer> preorder) {
        try {
            Topic topic = topics.get(preorder.get(0));
            preorder.remove(0);
            return topic.getTopicFromPreorderList(preorder);
        } catch (Exception e) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Creates an Agenda from a PreOrder list.
     *
     * @param preorder List of Strings
     *
     * @return Agenda
     */
    public Agenda getAgendaFromPreorderString(String preorder) {
        List<Integer> preorderList = getPreorderListFromPreorderString(preorder);
        try {
            lock.getReadAccess();
            return getAgendaFromPreorderList(preorderList);
        } catch (InterruptedException e) {
            return null;
        } finally {
            lock.finishRead();
        }
    }

    /**
     * Creates an Agenda from a PreOrder list.
     *
     * @param preorder List of Integers
     *
     * @return Agenda
     */
    private Agenda getAgendaFromPreorderList(List<Integer> preorder) {
        if(!preorder.isEmpty()) {
            preorder.remove(preorder.size() - 1);
            if(preorder.isEmpty()) {
                return this;
            } else {
                Topic topic = getTopicFromPreorderList(preorder);
                return topic.getSubTopics();
            }
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    public void register(AgendaObserver o) {
        observers.put(o, true);
    }

    @Override
    public void unregister(AgendaObserver o) {
        observers.remove(o);
    }

    @Override
    public void notifyObservers() {
        if(parent == null) {
            observers.forEachKey(2, o -> o.update(this));
        } else {
            parent.notifyObservers();
        }
    }

    @Override
    public String toString() {
        try {
            StringBuffer sb = new StringBuffer("{");
            lock.getReadAccess();
            if(topics.isEmpty()) {
                return "{}";
            } else {
                sb.append(topics.get(0).toString());
                for(int i = 1; i < topics.size(); i++) {
                    sb.append(", ").append(topics.get(i).toString());
                }
                sb.append("}");
                return sb.toString();
            }
        } catch (InterruptedException e) {
            return "";
        } finally {
            lock.finishRead();
        }

    }

    public ConcurrentHashMap<AgendaObserver, Boolean> getObservers() {
        return this.observers;
    }

    protected String getPreorder() {
        if(parent == null) {
            return "";
        } else {
            int i = 1;
            for(Topic t : parent.topics) {
                if(t.subTopics == this) {
                    break;
                }
                i++;
            }
            if(parent.getPreorder().isEmpty()) {
                return "" + i;
            } else {
                return parent.getPreorder() + "." + i;
            }
        }
    }
}
