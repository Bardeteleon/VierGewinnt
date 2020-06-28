module vierGewinnt {
	exports vierGewinnt;
	exports vierGewinnt.client;
	exports vierGewinnt.common;
	exports net;
	exports vierGewinnt.server;
	exports useful;
	exports vierGewinnt.client.animation;

	requires java.desktop;
	
	opens images.client;
	opens images.client.VAR1;
	opens images.client.Smileys;
	opens images.server;
}