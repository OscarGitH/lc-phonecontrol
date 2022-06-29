package org.lifecompanion.plugin.officialexample;

import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.plugin.PluginConfigPropertiesI;
import org.lifecompanion.model.api.plugin.PluginI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;

import java.io.File;
import java.util.List;
import java.util.Map;

public class ExamplePluginOfficial implements PluginI {
    public static final String ID = "lc-example-plugin";

    // RES
    //========================================================================
    @Override
    public String[] getLanguageFiles(final String languageCode) {
        return new String[]{"/text/" + languageCode + "_example_plugin.xml"};
    }

    @Override
    public String[] getJavaFXStylesheets() {
        return new String[]{"/style/example_plugin.css"};
    }
    //========================================================================


    // PLUGIN START/STOP
    //========================================================================
    @Override
    public void start(File dataDirectory) {

    }

    @Override
    public void stop(File dataDirectory) {

    }
    //========================================================================

    // MODE START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {

    }

    @Override
    public void modeStop(LCConfigurationI configuration) {

    }
    //========================================================================


    // VARIABLES
    //========================================================================
    @Override
    public List<UseVariableDefinitionI> getDefinedVariables() {
        return null;
    }

    @Override
    public Map<String, UseVariableI<?>> generateVariables(Map<String, UseVariableDefinitionI> variablesToGenerate) {
        return null;
    }
    //========================================================================

    // PLUGIN PROPERTIES
    //========================================================================
    @Override
    public PluginConfigPropertiesI newPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        return new ExamplePluginProperties(parentConfiguration);
    }
    //========================================================================
}
