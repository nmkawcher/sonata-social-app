## Fully Functional Social Media App for Android

**Here some images from the app.**

![](https://raw.githubusercontent.com/uzaysan/sonata-sociala-app/master/screenshots/unnamed.png)


**Features**

-  Authentication system. Users can register and login.

- Posting images or videos. User is limited to 4 images or 60 seconds of video per post.

- Searching users and posts.

- Users can like posts and make comments to posts.

- Comments can be upvoted or downvoted like Reddit. Users can comment with image and reply to comments.

- Built-in Admob support. App shows native ads in RecyclerView

- Posts and comments can be saved for later. (like a bookmark)

- Users can follow or block each other. App shows poss from users follows in main feed with some other suggested posts. And restrict users from seeing blocked profiles.

- Real-time messaging between users.

- Preloading images and videos before user scroll. Videos and images are also cached. And videos are auto played on scroll like Instagram.

- Explore section similar to Instagram.

- Multiple account support. So user can switch between account without entering password again.

- Supports both light and dark theme.

- And other regular social media features like mentions, hashtags etc..

## Set up Backend
- You need to use my backend repo for this app. (https://github.com/uzaysan/social-app-backend). Instructions on how to setup backend is written there.

## How to make app work?
- Once you setup backend, open `MyApp.java` file. And change applicationId and server url parameters with your server parameters.

- You need a firebase account for push notifications and other services to work such as analytics or crashlytics.

- Add googleservices.json file to app folder.

- You need to change appUrl variable to your domain to open posts or profiles via link. appUrl variable is in `Utils/Util.java`. You also need to change Url adress in manifiest file to your domain. In manifiest file, Url adres is set to `GuestProfileActivity` and `CommentActivity`

- If you want to show Admob Ads in RecyclerView, you need to change adId in strings.xml to your admob ad id. You also need to change App Id in manifiest file to you app id.
