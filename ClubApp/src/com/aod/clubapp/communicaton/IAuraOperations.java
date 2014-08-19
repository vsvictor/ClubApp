package com.aod.clubapp.communicaton;

import com.aod.clubapp.communicaton.datamodel.AuraDataSession;

/**
 * Interface provides by our communication service to UI (via Binder)
 *
 * @author Anatoliy Odukha <aodukha@gmail.com>
 */
public interface IAuraOperations {
	void executeSimpleGet(long requestId, String url);
	void executeSimplePut(long requestId, String url, String content);
	void executeSimplePost(long requestId, String url, String content);
	
	void initiateLogin(String user, String password);
	void initiateRegistration(String username, String email, String password);
	boolean isLoggedIn();
	boolean isAdmin();
	String getAuthTooken();
	void loadAlbums(String query, boolean force);
	void loadAlbumsNextPage(String query);
	void loadAlbums(long placeId);
	void loadAlbumsFollowedUsers(String query);
	AuraDataSession getDataSession();
	void loadCommentsForPhoto(long id);
	void postCommentForPhoto(long photoId, String commentBody);
	void likePhoto(long photoId);
	void unLikePhoto(long photoId);
	void reloadProfile();
	void reloadSearchOptions();
	void updateProfile();	
	void loadPlaceCategories();
	void loadPlacesInCategory(long category);
	void logout();
	void loadFollowedUsers();
	void loadNews();
	void loadDialogs();
	void markUserOnPhoto(long userId, long photoId, float x);
	void postDialogMessage(long dialogId, String msgBody);
	void subscribeFor(long userId);
	void unsubscribeFrom(long userId);
	void deleteChatMessage(long msgId);
	void sendRegId(String regId);
	void loadInterviewAlbums(String query);
}
