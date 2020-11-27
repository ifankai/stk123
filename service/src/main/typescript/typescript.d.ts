// Generated using typescript-generator version 1.25.322 on 2020-11-27 12:04:28.

export interface StkTextEntity extends Serializable {
    id: number;
    type: number;
    code: string;
    codeType: number;
    title: string;
    text: string;
    insertTime: Date;
    updateTime: Date;
    dispOrder: number;
    userId: number;
    subType: number;
    userName: string;
    userAvatar: string;
    followersCount: number;
    createdAt: Date;
    postId: number;
    replyCount: number;
    favoriteDate: Date;
    readDate: Date;
}

export interface RequestResult<T> extends Serializable {
    success: boolean;
    msg: string;
    code: number;
    data: T;
}

export interface Serializable {
}
