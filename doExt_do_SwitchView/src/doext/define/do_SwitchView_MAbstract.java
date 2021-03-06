package doext.define;

import core.object.DoUIModule;
import core.object.DoProperty;
import core.object.DoProperty.PropertyDataType;


public abstract class do_SwitchView_MAbstract extends DoUIModule{

	protected do_SwitchView_MAbstract() throws Exception {
		super();
	}
	
	/**
	 * 初始化
	 */
	@Override
	public void onInit() throws Exception{
        super.onInit();
        //注册属性
		this.registProperty(new DoProperty("checked", PropertyDataType.Bool, "false", false));
		this.registProperty(new DoProperty("colors", PropertyDataType.String, "00FF00,888888,FFFFFF", true));
		this.registProperty(new DoProperty("shape", PropertyDataType.String, "circle", true));
	}
}