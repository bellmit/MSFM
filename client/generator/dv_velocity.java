import java.io.StringWriter;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.VelocityContext;

public class dv_velocity
{
    public static void main(String args[]) throws Exception
    {
        /* first, we init the runtime engine.  Defaults are fine. */
        Velocity.init();

        /* lets make a Context and put data into it */
        VelocityContext context = new VelocityContext();

        context.put("KEY_TYPE",    args.length > 1 ? args[1] : "");
        context.put("VALUE_TYPE",  args.length > 2 ? args[2] : "");
        context.put("THIRD_TYPE",  args.length > 3 ? args[3] : "");
        context.put("FOURTH_TYPE", args.length > 4 ? args[4] : "");

        /* lets render a template */
        StringWriter w = new StringWriter();

        Velocity.mergeTemplate(args[0], context, w);

        System.out.println(w);
    }
}
