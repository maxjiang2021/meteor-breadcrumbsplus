package yourmom.breadcrumbsplus;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Modules;
import yourmom.breadcrumbsplus.modules.general.Breadcrumbs;

import org.slf4j.Logger;

public class BreadCrumbsPlus extends MeteorAddon {
	// public static final Category CATEGORY = new Category("Numby Hack", Items.TURTLE_HELMET.getDefaultStack());
	public static final Logger LOGGER = LogUtils.getLogger();

	@Override
	public void onInitialize() {
		Modules.get().add(new Breadcrumbs());
	}

	@Override
	public void onRegisterCategories() {
		// Modules.registerCategory(CATEGORY);
	}

	@Override
	public String getPackage() {
		return "yourmom.breadcrumbsplus";
	}

	public static void Log(String text) {
		LOGGER.info(text);
	}
}
