// Generated using typescript-generator version 1.25.322 on 2020-11-15 20:39:20.

export interface XqPost extends Serializable {
    id: number;
    title: string;
    text: string;
    createdAt: Date;
    replyCount: number;
    followersCount: number;
    insertDate: Date;
    isFavorite: boolean;
    favoriteDate: Date;
    isRead: boolean;
    readDate: Date;
    userId: number;
    userName: string;
    userAvatar: string;
}

export interface RequestResult<T> extends Serializable {
    success: boolean;
    msg: string;
    code: number;
    data: T;
}

export interface Serializable {
}
