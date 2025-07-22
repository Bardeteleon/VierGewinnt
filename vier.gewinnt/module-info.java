module vier.gewinnt {
	requires java.desktop;
	
	opens images.client;
	opens images.client.VAR1;
	opens images.client.Smileys;
	opens images.server;
}