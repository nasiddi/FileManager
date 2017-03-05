package screen;

import controller.Controller;
import model.Model;
import panel.Panel;
import panel.SwingPanel;

public abstract class SwingScreen<PanelType extends SwingPanel, ModelType extends Model, ControllerType extends Controller> extends Screen<PanelType, ModelType, ControllerType> {
    public SwingScreen(PanelType panel, ModelType model, ControllerType controller) {
        super(panel, model, controller);
    }
}
