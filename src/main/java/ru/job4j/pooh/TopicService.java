package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ru.job4j.pooh.Req.GET;
import static ru.job4j.pooh.Req.POST;
import static ru.job4j.pooh.Resp.STATUS_200;
import static ru.job4j.pooh.Resp.STATUS_204;

public class TopicService implements Service {

    private final Map<String, ConcurrentHashMap<String, ConcurrentLinkedQueue<String>>> topics = new ConcurrentHashMap<>();
    @Override
    public Resp process(Req req) {
        Resp rsl = null;
        String sourceName = req.getSourceName();
        String reqType = req.httpRequestType();
        String reqParam = req.getParam();

        if (POST.equals(reqType) && topics.get(sourceName) == null) {
            rsl = new Resp("Топик с названием " + sourceName + " - недоступен", STATUS_204);
        }

        if (POST.equals(reqType) && topics.get(sourceName) != null) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = topics.get(sourceName);
            for (ConcurrentLinkedQueue<String> queue : topic.values()) {
                queue.add(reqParam);
            }
            rsl = new Resp(reqParam, STATUS_200);
        }

        if (GET.equals(reqType)) {
            ConcurrentHashMap<String, ConcurrentLinkedQueue<String>> topic = topics.get(sourceName);
            String text;
            if (topic == null) {
                topics.putIfAbsent(sourceName, new ConcurrentHashMap<>());
                topic = topics.get(sourceName);
            }
            ConcurrentLinkedQueue<String> subscriberQueue = topic.get(reqParam);
            if (subscriberQueue == null) {
                topic.putIfAbsent(reqParam, new ConcurrentLinkedQueue<>());
                text = "";
            } else {
                ConcurrentLinkedQueue<String> concurrentLinkedQueue = topic.get(reqParam);
                text = concurrentLinkedQueue.poll();
            }
            rsl = new Resp(text, STATUS_200);
        }

        return rsl;
    }
}

