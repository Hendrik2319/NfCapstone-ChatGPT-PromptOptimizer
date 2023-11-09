import styled from "styled-components";

export const Label = styled.label`
  color: var(--text-color-label);
  font-size: 0.8em;
`;

export const BigLabel = styled.label`
  color: var(--text-color-biglabel);
  font-size: 1em;
`;

export const Id = styled.div`
  color: var(--text-color-less);
  font-size: 0.5em;
`;

export const SimpleCard = styled.div`
  border: 1px solid var(--border-color, #707070);
  border-radius: 0.2em;
  padding: 0.2em;
  background: var(--background-color);
`;

export const MainCard = styled(SimpleCard)`
  border-radius: 0.4em;
  padding: 1em;
`;
