import { addMonths, format, subMonths } from "date-fns";
import { useEffect, useState } from "react";
import AccountMonthlyChart from "./AccountMonthlyChart";
import {
  CategoryByMonthMap,
  accountByMonthResponse,
} from "../../types/accountType";
import { getAccountByCategory, getAccountMonthly } from "../../apis/accountApi";
import {
  BackArrowIcon,
  ForwardArrowIcon,
} from "../common/CommonStyledComponents";
import styled from "styled-components";

const AccountMonthly = () => {
  const [currentMonth, setCurrentMonth] = useState(new Date());
  const currentDate = format(currentMonth, "M");
  const formatDate = format(currentMonth, "yyyy-MM");
  const [outputByMonth, setOutputByMonth] = useState<CategoryByMonthMap>({
    식비: 0,
    카페: 0,
    생활: 0,
    교통: 0,
    기타: 0,
  });
  const [inOutByMonth, setInOutByMonth] = useState<number[]>([0, 0]);

  const prevMonth = () => {
    setCurrentMonth(subMonths(currentMonth, 1));
  };
  const nextMonth = () => {
    setCurrentMonth(addMonths(currentMonth, 1));
  };

  const extractInOutValue = (object: accountByMonthResponse): number[] => {
    return [object.totalInput ?? 0, Math.abs(object.totalOutput ?? 0)];
  };

  useEffect(() => {
    const fetchData = async () => {
      // 월별 거래 금액 조회 account/month
      const accountMonthlyData: accountByMonthResponse[] | null =
        await getAccountMonthly(format(currentMonth, "yyyy-MM"));
      if (accountMonthlyData !== null) {
        setInOutByMonth(extractInOutValue(accountMonthlyData[0]));
      }

      // 월별 지출 카테고리별 금액 조회
      const accountByCategoryData: CategoryByMonthMap | null =
        await getAccountByCategory(format(currentMonth, "yyyy-MM"));
      if (accountByCategoryData !== null) {
        setOutputByMonth(accountByCategoryData);
      }
    };

    fetchData();

    console.log(formatDate);
  }, [currentMonth, formatDate]);

  return (
    <div>
      <MonthContainer>
        <BackArrowIcon size={24} onClick={prevMonth} />
        <div>{currentDate}월</div>
        <ForwardArrowIcon size={24} onClick={nextMonth} />
      </MonthContainer>
      <AccountMonthlyChart outputs={outputByMonth} inOutValues={inOutByMonth} />
    </div>
  );
};

export default AccountMonthly;

const MonthContainer = styled.div`
  font-size: 1.3rem;
  display: flex;
  align-items: center;
  width: 100px;
  justify-content: space-between;
  margin: 1rem 0;
`;
