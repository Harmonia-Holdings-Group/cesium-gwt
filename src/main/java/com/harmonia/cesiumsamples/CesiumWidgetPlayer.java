package com.harmonia.cesiumsamples;

import com.google.gwt.animation.client.AnimationScheduler;
import com.google.gwt.animation.client.AnimationScheduler.AnimationCallback;
import com.google.gwt.animation.client.AnimationScheduler.AnimationHandle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.SimplePanel;
import com.harmonia.gwt.cesium.BingMapsImageryProviderOptions;
import com.harmonia.gwt.cesium.CesiumConfiguration;
import com.harmonia.gwt.cesium.CesiumWidget;
import com.harmonia.gwt.cesium.CesiumWidgetOptions;
import com.harmonia.gwt.cesium.CesiumWidgetPanel;
import com.harmonia.gwt.cesium.ImageryProvider;
import com.harmonia.gwt.cesium.Scene;
import com.harmonia.gwt.cesium.ScreenSpaceCameraController;
import com.harmonia.gwt.cesium.TerrainProvider;

public class CesiumWidgetPlayer extends CesiumWidgetPanel {

	private CesiumWidget cesiumWidget;
  private AnimationHandle loop;
//  private static double lastTime = 0.0;

	/**
	 * 
	 */
	public CesiumWidgetPlayer(CesiumConfiguration configuration) {
    super(configuration);
	}

	/**
	 * @return the cesiumWidget
	 */
	public CesiumWidget getCesiumWidget() {
		return cesiumWidget;
	}

	@Override
	public CesiumWidget createCesiumWidget(Element element) {
		
		BingMapsImageryProviderOptions bingMapOptions = BingMapsImageryProviderOptions.create()
      .setMapStyleAerialWithLabels();
		
    if (getConfiguration().getBingMapsKey() != null) {
      bingMapOptions
  			.setKey(getConfiguration().getBingMapsKey());
    }
      
		ImageryProvider bingMaps = ImageryProvider.createBingMapsImageryProvider(
      bingMapOptions
		);
		
		cesiumWidget = CesiumWidget.create(element,
			CesiumWidgetOptions.create()
				.setImageryProvider(bingMaps)
				.setTerrainProvider(TerrainProvider.createCesiumTerrainProvider())
	      .setUseDefaultRenderLoop(false)
		);
    
    Scene scene = cesiumWidget.getScene();
    ScreenSpaceCameraController screenSpaceCameraController = 
    		ScreenSpaceCameraController.create(cesiumWidget.getCanvas(), scene.getCamera());
    scene.overrideScreenSpaceCameraController(screenSpaceCameraController);
    
    pickCartographicPosition(cesiumWidget);
    
    return cesiumWidget;
	}
  
	public void play() {
    if (loop != null) {
    	return;
    }
    
    final AnimationScheduler scheduler = AnimationScheduler.get();
    
    loop = scheduler.requestAnimationFrame(new AnimationCallback() {
      
			public void execute(double timestamp) {
				// I suggest doing something like this when "idle" (not moving the mouse or wheel)
        //  because the animation loop eats up a lot of CPU cycles.
        //if ((timestamp - lastTime) > 100.0) {
          //lastTime = timestamp;
          if (cesiumWidget != null) {
            cesiumWidget.resize();
            cesiumWidget.render();
          }
        //}
        loop = scheduler.requestAnimationFrame(this);
			}
            	
    });
	}
  
	public void pause() {
    if (loop != null) {
    	loop.cancel();
    	loop = null;
    }
	}
  
  /**
   * Example of using Cesium JavaScript when desired. However it shouldn't be very difficult
   * to implement this in GWT Java.
   * @param cesiumWidget
   */
  private final native void pickCartographicPosition(CesiumWidget cesiumWidget) /*-{
    
    //var cesiumWidget = this
    
    var otherLoaders = cesiumWidget.onload;
    
    if (otherLoaders != null) {
      otherLoaders();
    }
        
    var scene = cesiumWidget.scene;
    var ellipsoid = scene.globe.ellipsoid;
      
    var scaleFactor = 1;
    var pixelRatio = 1;
    if (typeof $wnd.devicePixelRatio !== 'undefined') {
      pixelRatio = $wnd.devicePixelRatio;
      scaleFactor *= pixelRatio;
    }
      
    var fontPixels = 18;
  
    var labels = new Cesium.LabelCollection();
    label = labels.add({
      font : (fontPixels*scaleFactor)+'px sans-serif'
    });
    scene.primitives.add(labels);
  
    // Mouse over the globe to see the cartographic position
    handler = new Cesium.ScreenSpaceEventHandler(scene.canvas);
      
    handler.setInputAction(function(movement) {
        var cartesian = scene.camera.pickEllipsoid(movement.endPosition, ellipsoid);
        if (cartesian) {
            var cartographic = ellipsoid.cartesianToCartographic(cartesian);
            label.show = true;
            label.text = '(' + Cesium.Math.toDegrees(cartographic.latitude).toFixed(2) + ', ' + Cesium.Math.toDegrees(cartographic.longitude).toFixed(2) + ')';
            label.position = cartesian;
        } else {
            label.text = '';
        }
    }, Cesium.ScreenSpaceEventType.MOUSE_MOVE);
  }-*/;
}