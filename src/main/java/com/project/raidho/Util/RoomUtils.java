package com.project.raidho.Util;

import java.util.Random;

public class RoomUtils {

    private static final Random random = new Random(System.currentTimeMillis());

    private static final String[] DEFAULT_ROOM_PICS = {
            "https://cdn.pixabay.com/photo/2016/11/21/17/44/arches-national-park-1846759__480.jpg",
            "https://cdn.pixabay.com/photo/2020/04/25/01/36/road-5089188__480.jpg",
            "https://cdn.pixabay.com/photo/2020/07/08/10/17/car-5383371__340.jpg",
            "https://cdn.pixabay.com/photo/2019/08/14/10/37/beach-4405371__480.jpg",
            "https://cdn.pixabay.com/photo/2019/04/04/18/29/hamburg-4103406__480.jpg"
    };

    public static String getRandomRoomPic() {
        return DEFAULT_ROOM_PICS[random.nextInt(DEFAULT_ROOM_PICS.length)];
    }
}
