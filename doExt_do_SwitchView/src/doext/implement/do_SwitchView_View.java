package doext.implement;

import java.util.Map;

import android.content.Context;
import android.widget.CompoundButton;
import android.widget.Switch;
import core.helper.DoTextHelper;
import core.helper.DoUIModuleHelper;
import core.helper.jsonparse.DoJsonNode;
import core.interfaces.DoIScriptEngine;
import core.interfaces.DoIUIModuleView;
import core.object.DoInvokeResult;
import core.object.DoUIModule;
import doext.define.do_SwitchView_IMethod;
import doext.define.do_SwitchView_MAbstract;

/**
 * 自定义扩展UIView组件实现类，此类必须继承相应VIEW类，并实现DoIUIModuleView,do_SwitchView_IMethod接口；
 * #如何调用组件自定义事件？可以通过如下方法触发事件：
 * this.model.getEventCenter().fireEvent(_messageName, jsonResult);
 * 参数解释：@_messageName字符串事件名称，@jsonResult传递事件参数对象；
 * 获取DoInvokeResult对象方式new DoInvokeResult(this.getUniqueKey());
 */
public class do_SwitchView_View extends Switch implements DoIUIModuleView, do_SwitchView_IMethod{
	
	/**
	 * 每个UIview都会引用一个具体的model实例；
	 */
	private do_SwitchView_MAbstract model;
	

	public do_SwitchView_View(Context context) {
		super(context);
	}
	
	/**
	 * 初始化加载view准备,_doUIModule是对应当前UIView的model实例
	 */
	@Override
	public void loadView(DoUIModule _doUIModule) throws Exception {
		this.model = (do_SwitchView_MAbstract)_doUIModule;
		this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DoInvokeResult jsonResult = new DoInvokeResult(model.getUniqueKey());
				jsonResult.setResultBoolean(isChecked);
				model.getEventCenter().fireEvent("changed", jsonResult);
				
			}
		});
	}
	
	/**
	 * 动态修改属性值时会被调用，方法返回值为true表示赋值有效，并执行onPropertiesChanged，否则不进行赋值；
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public boolean onPropertiesChanging(Map<String, String> _changedValues) {
		return true;
	}
	
	/**
	 * 属性赋值成功后被调用，可以根据组件定义相关属性值修改UIView可视化操作；
	 * @_changedValues<key,value>属性集（key名称、value值）；
	 */
	@Override
	public void onPropertiesChanged(Map<String, String> _changedValues) {
		DoUIModuleHelper.handleBasicViewProperChanged(this.model, _changedValues);
		if(_changedValues.containsKey("checked")){
			this.setChecked(DoTextHelper.strToBool(_changedValues.get("checked"), false));
		}
	}
	
	/**
	 * 同步方法，JS脚本调用该组件对象方法时会被调用，可以根据_methodName调用相应的接口实现方法；
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前Page JS上下文环境对象
	 * @_invokeResult 用于返回方法结果对象
	 */
	@Override
	public boolean invokeSyncMethod(String _methodName, DoJsonNode _dictParas,
			DoIScriptEngine _scriptEngine, DoInvokeResult _invokeResult)throws Exception {
		//...do something
		return false;
	}
	
	/**
	 * 异步方法（通常都处理些耗时操作，避免UI线程阻塞），JS脚本调用该组件对象方法时会被调用，
	 * 可以根据_methodName调用相应的接口实现方法；
	 * @_methodName 方法名称
	 * @_dictParas 参数（K,V）
	 * @_scriptEngine 当前page JS上下文环境
	 * @_callbackFuncName 回调函数名
	 * #如何执行异步方法回调？可以通过如下方法：
	 *	_scriptEngine.callback(_callbackFuncName, _invokeResult);
	 * 参数解释：@_callbackFuncName回调函数名，@_invokeResult传递回调函数参数对象；
	   获取DoInvokeResult对象方式new DoInvokeResult(this.getUniqueKey());
	 */
	@Override
	public boolean invokeAsyncMethod(String _methodName, DoJsonNode _dictParas,
			DoIScriptEngine _scriptEngine, String _callbackFuncName) {
		//...do something
		return false;
	}
	
	/**
	* 释放资源处理，前端JS脚本调用closePage或执行removeui时会被调用；
	*/
	@Override
	public void onDispose() {
		//...do something
	}
	
	/**
	* 重绘组件，构造组件时由系统框架自动调用；
	  或者由前端JS脚本调用组件onRedraw方法时被调用（注：通常是需要动态改变组件（X、Y、Width、Height）属性时手动调用）
	*/
	@Override
	public void onRedraw() {
		this.setLayoutParams(DoUIModuleHelper.getLayoutParams(this.model));
	}
	
	/**
	 * 获取当前model实例
	 */
	@Override
	public DoUIModule getModel() {
		return model;
	}
}