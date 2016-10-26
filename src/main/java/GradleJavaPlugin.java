
import org.gradle.api.Project;
import org.gradle.api.Plugin;

public class GradleJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
        target.getTasks().create("dumpStringsToTranslations (Export XML Translations)", TaskDumpStringsToTranslations.class);
        target.getTasks().create("dumpTranslationsToStrings (Import XML Translations)", TaskDumpTranslationsToStrings.class);
    }
}