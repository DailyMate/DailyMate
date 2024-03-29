import { AxiosResponse } from "axios";
import { API } from "./api";
import {
  CategoryByMonthMap,
  accountByDateResponse,
  accountByMonthResponse,
  accountRequest,
} from "../types/accountType";

export const addAccount = async (body: accountRequest) => {
  console.log(body);
  try {
    const res: AxiosResponse<{ message: string }> = await API.post(
      "/account",
      body
    );
    return res.data.message;
  } catch (error) {
    console.error("내역 등록 오류 : ", error);
    return null;
  }
};

export const modifyAccount = async (
  body: accountRequest,
  accountId: number
) => {
  console.log(accountId, body);
  try {
    const res: AxiosResponse<{ message: string }> = await API.patch(
      `/account/${accountId}`,
      body
    );
    return res.data.message;
  } catch (error) {
    console.error("내역 수정 오류 : ", error);
    return null;
  }
};

export const deleteAccount = async (accountId: number) => {
  try {
    const res: AxiosResponse<{ message: string }> = await API.delete(
      `/account/${accountId}`
    );
    return res.data.message;
  } catch (error) {
    console.error("내역 삭제 오류 : ", error);
    return null;
  }
};

export const getAccountMonthly = async (date: string) => {
  try {
    const res = await API.get<accountByMonthResponse[]>("/account/month", {
      params: {
        date: date,
      },
    });
    return res.data;
  } catch (error) {
    console.error("월별 거래 금액 조회 오류 : ", error);
    return null;
  }
};

export const getAccountByCategory = async (date: string) => {
  try {
    const res = await API.get<CategoryByMonthMap>(`/account/category/map`, {
      params: {
        date: date,
      },
    });
    return res.data;
  } catch (error) {
    console.error("월별 지출 카테고리별 조회 오류 : ", error);
    return null;
  }
};

export const getAccountByDate = async (date: string) => {
  try {
    const res = await API.get<accountByDateResponse[]>(`/account`, {
      params: {
        date: date,
      },
    });
    return res.data;
  } catch (error) {
    console.error("날짜별 거래 내역조회 오류 : ", error);
    return null;
  }
};
