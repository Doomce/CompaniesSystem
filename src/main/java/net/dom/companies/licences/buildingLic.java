package net.dom.companies.licences;

import org.bukkit.Material;
import org.jetbrains.annotations.Nullable;

import java.time.Clock;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.List;

public class buildingLic extends Licence {

    private LocalDate expiration;

    public buildingLic() {
        super(0, "Leidimas vykdyti statybas", 840.00, 1, Material.BRICKS);
    }

    @Override
    public List<String> getDescription() {
        return List.of("Įgalinamas darbas statybų aikštelėje.");
    }
}
