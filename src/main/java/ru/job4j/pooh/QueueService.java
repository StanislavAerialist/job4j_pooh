package ru.job4j.pooh;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import static ru.job4j.pooh.Req.GET;
import static ru.job4j.pooh.Req.POST;

public class QueueService implements Service {
    private final Map<String, ConcurrentLinkedQueue<String>> queue = new ConcurrentHashMap<>();
    @Override
    public Resp process(Req req) {
        Resp rsl = null;
        String sourceName = req.getSourceName();
        String reqType = req.httpRequestType();
        String reqParam = req.getParam();

        if (POST.equals(reqType)) {
            ConcurrentLinkedQueue<String> concurrentLinkedQueue = queue.getOrDefault(sourceName, new ConcurrentLinkedQueue<>());
            concurrentLinkedQueue.add(reqParam);
            queue.putIfAbsent(sourceName, concurrentLinkedQueue);
            rsl = new Resp(reqParam, "Статус 200");
        }

        if ((GET.equals(reqType) && queue.get(sourceName) == null) || queue.get(sourceName).isEmpty()) {
            rsl = new Resp("Очередь с именем " + sourceName + " - не существует или пуста", "Статус 204");
        }

        if (GET.equals(reqType) && queue.get(sourceName) != null && !queue.get(sourceName).isEmpty()) {
            rsl = new Resp(queue.get(sourceName).poll(), "Статус 200");
        }
        return rsl;
    }
}