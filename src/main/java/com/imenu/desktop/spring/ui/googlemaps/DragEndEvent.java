package com.imenu.desktop.spring.ui.googlemaps;

import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;

@SuppressWarnings("serial")
@DomEvent("google-map-marker-dragend")
public class DragEndEvent extends ComponentEvent<GoogleMapMarker> {

	public DragEndEvent(GoogleMapMarker source, boolean fromClient) {
		super(source, fromClient);
	}

}