package pcu.util;


import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A servlet for incrementing a configurable sharded counter.
 */
@SuppressWarnings("serial")
@WebServlet(name = "counter", urlPatterns = { "/api/counter" })
public class ShardedCounterServlet extends HttpServlet {
    @Override
    public final void doGet(final HttpServletRequest req,
            final HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");

        String counterName = req.getParameter("name");
        String action = req.getParameter("action");
        String shards = req.getParameter("shards");

        ShardedCounter counter = new ShardedCounter(counterName);

        if ("increment".equals(action)) {
            counter.increment();
            resp.getWriter().println("Counter incremented.");
        } else if ("increase_shards".equals(action)) {
            int inc = Integer.valueOf(shards);
            counter.addShards(inc);
            resp.getWriter().println("Shard count increased by " + inc + ".");
        } else {
            resp.getWriter().println("getCount() -> " + counter.getCount());
        }
    }
}