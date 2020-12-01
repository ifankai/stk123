// Generated using typescript-generator version 1.25.322 on 2020-12-01 11:30:49.

export interface StkTextEntity extends Serializable {
    id: number;
    type: number;
    code: string;
    codeType: number;
    title: string;
    text: string;
    textDesc: string;
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
    code: number;
    data: T;
}

export interface PageRoot<T> extends Serializable {
    about: string;
    count: number;
    key: string;
    list: T[];
    perPage: number;
    maxPage: number;
    page: number;
}

export interface Serializable {
}
