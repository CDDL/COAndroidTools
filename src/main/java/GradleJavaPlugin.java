
import org.gradle.api.Project;
import org.gradle.api.Plugin;

public class GradleJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getTasks().create("dumpStringsToTranslations", TaskDumpStringsToTranslations.class);
    }
}