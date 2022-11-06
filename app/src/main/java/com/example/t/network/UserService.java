package com.example.t.network;

import com.example.t.model.Inventory;
import com.example.t.model.Item;
import com.example.t.model.LoginReq;
import com.example.t.model.LoginRes;
import com.example.t.model.Room;
import com.example.t.model.Space;
import com.example.t.model.StorageSpace;
import com.example.t.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;


public interface UserService {

    @POST("login/")
    Call<LoginRes> login(@Body User user);

    @POST("users/")
    Call<User> register(@Body User user);

    @GET("items/{user_id}")
    Call<List<Item>> getItems(@Path(value = "user_id") int userId);

    @POST("items/{user_id}")
    Call<List<Item>> getItemsBySpace(@Path(value = "user_id") int userId, @Body Space space);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("users/{user_id}/items/")
    Call<Item> postItem(@Path(value = "user_id") int userId , @Body Item item);

    @DELETE("items/{item_id}")
    Call<Item> deleteItem(@Path(value = "item_id") int itemId);

    @GET("rooms/{user_id}")
    Call<List<Room>> getRooms(@Path(value = "user_id") int userId);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("users/{user_id}/rooms/")
    Call<Room> postRoom(@Path(value = "user_id") int userId , @Body Room room);

    @DELETE("rooms/{room_id}")
    Call<Room> deleteRoom(@Path(value = "room_id") int roomId);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("storage_spaces/{user_id}")
    Call<List<StorageSpace>> getStorageSpaces(@Path(value = "user_id") int userId, @Body StorageSpace storageSpace);

    @GET("storage_spaces/{user_id}")
    Call<List<StorageSpace>> getAllStorageSpaces(@Path(value = "user_id") int userId);

    @Headers({"Content-type:application/json;charset=UTF-8"})
    @POST("users/{user_id}/storage_spaces/")
    Call<StorageSpace> postStorageSpace(@Path(value = "user_id") int userId , @Body StorageSpace space);

    @DELETE("storage_spaces/{storage_space_id}")
    Call<StorageSpace> deleteStorageSpace(@Path(value = "storage_space_id") int storageSpaceId);

    @POST("users/{user_id}/ivt/")
    Call<Inventory> postInventory(@Path(value = "user_id") int userId, @Body Inventory ivt);

    @POST("ivt/{user_id}")
    Call<Inventory> getItemInventory(@Path(value = "user_id") int userId, @Body Inventory ivt);

    @POST("ivt/space/{user_id}")
    Call<List<Inventory>> getInventoryBySpace(@Path(value = "user_id") int userId, @Body Inventory ivt);

    @Multipart
    @POST("upload/pic/")
    Call<String> uploadPic(@Part("FunName") RequestBody funName, @Part MultipartBody.Part file);

    @Multipart
    @POST("upload/audio/")
    Call<String> uploadAudio(@Part("FunName") RequestBody funName, @Part MultipartBody.Part file);

    @DELETE("ivt/{item_id}")
    Call<Inventory> deleteInventory(@Path(value = "item_id") int itemId);
}