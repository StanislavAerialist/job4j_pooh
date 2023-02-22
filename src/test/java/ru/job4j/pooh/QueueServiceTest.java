package ru.job4j.pooh;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class QueueServiceTest {

    @Test
    public void whenPostThenGetQueue() {
        QueueService queueService = new QueueService();
        String paramForPostMethod = "temperature=18";
        /* Добавляем данные в очередь weather. Режим queue */
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod)
        );
        /* Забираем данные из очереди weather. Режим queue */
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("temperature=18"));
        assertThat(result.status(), is("200"));
    }

    @Test
    public void whenGetEmptyQueue() {
        QueueService queueService = new QueueService();
        Resp result = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result.text(), is("Очередь с именем weather - не существует или пуста"));
        assertThat(result.status(), is("204"));
    }

    @Test
    public void when2PostAnd3Get() {
        QueueService queueService = new QueueService();
        String paramForPostMethod1 = "temperature=18";
        String paramForPostMethod2 = "temperature=-18";
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod1)
        );
        queueService.process(
                new Req("POST", "queue", "weather", paramForPostMethod2)
        );
        Resp result1 = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        Resp result2 = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        Resp result3 = queueService.process(
                new Req("GET", "queue", "weather", null)
        );
        assertThat(result1.text(), is("temperature=18"));
        assertThat(result1.status(), is("200"));
        assertThat(result2.text(), is("temperature=-18"));
        assertThat(result2.status(), is("200"));
        assertThat(result3.text(), is("Очередь с именем weather - не существует или пуста"));
        assertThat(result3.status(), is("204"));
    }
}