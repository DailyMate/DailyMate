import styled from "styled-components";
import { AmountNumber } from "../common/CommonStyledComponents";

interface props {
  totalInput: number | null;
  totalOutput: number | null;
}

const InOutMonthly = ({ totalInput, totalOutput }: props) => {
  return (
    <InOutWrapper>
      <InOutContainer>
        <AmountNumber textType="text">수입</AmountNumber>
        <AmountNumber textType="in">{totalInput}원</AmountNumber>
      </InOutContainer>
      <InOutContainer>
        <AmountNumber textType="text">지출</AmountNumber>
        <AmountNumber textType="out">{totalOutput}원</AmountNumber>
      </InOutContainer>
    </InOutWrapper>
  );
};

export default InOutMonthly;

const InOutWrapper = styled.div`
  display: flex;
  justify-content: space-around;
`;

const InOutContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
`;