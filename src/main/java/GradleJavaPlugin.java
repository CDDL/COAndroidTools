
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import i18nViews.TaskGenerateI18nViews;

public class GradleJavaPlugin implements Plugin<Project> {

    @Override
    public void apply(Project target) {
//        target.getTasks().create("dumpStringsToTranslations (Export XML Translations)", TaskDumpStringsToTranslations.class);
//        target.getTasks().create("dumpTranslationsToStrings (Import XML Translations)", TaskDumpTranslationsToStrings.class);
        target.getTasks().create("generateI18nViews", TaskGenerateI18nViews.class);
    }
}