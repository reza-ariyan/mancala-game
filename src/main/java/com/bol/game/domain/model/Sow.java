package com.bol.game.domain.model;


import com.bol.game.domain.WellKnown;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.UUID;

import static com.bol.game.domain.WellKnown.Empty;
import static com.bol.game.domain.WellKnown.TOTAL_PITS;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sow {
    @NotNull
    public UUID gameId;

    @NotBlank
    public int player;

    @Min(Empty)
    @Max(TOTAL_PITS)
    public int pitNumber;
}