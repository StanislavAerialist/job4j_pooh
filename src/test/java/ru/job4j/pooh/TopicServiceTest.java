package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TopicServiceTest {

    @Test
    public void whenTopic() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        String paramForSubscriber2 = "client6565";
        /* Режим topic. Подписываемся на топик weather. client407. */
        topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Добавляем данные в топик weather. */
        topicService.process(
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather. Очередь client407. */
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        /* Режим topic. Пытаемся забрать данные из индивидуальной очереди в топике weather. Очередь client6565.
        Эта очередь отсутствует, т.к. client6565 еще не был подписан, поэтому он получит пустую строку. Будет создана индивидуальная очередь для client6565 */
        Resp result2 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber2)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result2.text(), is(""));
    }

    @Test
    public void whenPostWithoutGet() {
        TopicService topicService = new TopicService();
        String paramForPublisher = "temperature=18";
        String paramForSubscriber1 = "client407";
        Resp result = topicService.process(
                /* Режим topic. Пытаемся добавить данные в топик weather без подписки. */
                new Req("POST", "topic", "weather", paramForPublisher)
        );
        Resp result1 = topicService.process(
                new Req("GET", "topic", "weather", paramForSubscriber1)
        );
        assertThat(result.text(), is("Топик с названием weather - недоступен"));
        assertThat(result.status(), is("204"));
        assertThat(result1.text(), is(""));
    }

    @Test
    public void when2PostAnd2GetIn2Topics() {
        TopicService topicService = new TopicService();
        String paramForPublisher1 = "temperature=18";
        String paramForPublisher2 = "temperature=-18";
        String paramForSubscriber1 = "client407";
        /* Режим topic. Подписываемся на топик weather1. client407. */
        topicService.process(
                new Req("GET", "topic", "weather1", paramForSubscriber1)
        );
        /* Режим topic. Подписываемся на топик weather2. client407. */
        topicService.process(
                new Req("GET", "topic", "weather2", paramForSubscriber1)
        );
        /* Режим topic. Добавляем данные в топик weather1. */
        Resp result1 = topicService.process(
                new Req("POST", "topic", "weather1", paramForPublisher1)
        );
        /* Режим topic. Добавляем данные в топик weather2. */
        Resp result2 = topicService.process(
                new Req("POST", "topic", "weather2", paramForPublisher2)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather1. Очередь client407. */
        Resp result3 = topicService.process(
                new Req("GET", "topic", "weather1", paramForSubscriber1)
        );
        /* Режим topic. Забираем данные из индивидуальной очереди в топике weather2. Очередь client407. */
        Resp result4 = topicService.process(
                new Req("GET", "topic", "weather2", paramForSubscriber1)
        );
        assertThat(result1.text(), is(paramForPublisher1));
        assertThat(result1.status(), is("200"));
        assertThat(result2.text(), is(paramForPublisher2));
        assertThat(result2.status(), is("200"));
        assertThat(result3.text(), is(paramForPublisher1));
        assertThat(result3.status(), is("200"));
        assertThat(result4.text(), is(paramForPublisher2));
        assertThat(result4.status(), is("200"));
    }
}