package process;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CalcDiscount {

    //Proof of concept

    // 56% OFF, 87%off, 1% Off
    private static final Pattern PERCENT_PATTERN = Pattern.compile("(\\d+)%\\s*[oO][fF][fF]");

    //2+1 , 1+1 50%
    private static final Pattern QUANTITY_PATTERN = Pattern.compile("(\\d+)\\s*\\+\\s*(\\d+)(?:\\s+(\\d+)%\\s*off)?", Pattern.CASE_INSENSITIVE);



    public static float calcDiscount(Float price, String sale, int quantity) {

        if (sale == null || sale.isBlank()) return price;

        Matcher matcher = PERCENT_PATTERN.matcher(sale);
        if (matcher.find()) {
            return byPercentages(price, matcher) * quantity;
        }

        matcher = QUANTITY_PATTERN.matcher(sale);
        if (matcher.find()) {
            return byQuantity(price,quantity,matcher);
        }


        return price;
    }


    private static Float byPercentages(Float price, Matcher matcher) {
         float discount = Float.parseFloat(matcher.group(1));
         return price * (1 - discount / 100);
    }

    private static Float byQuantity(Float unitPrice, int totalQuantity, Matcher matcher) {
        int buy = Integer.parseInt(matcher.group(1));
        int get = Integer.parseInt(matcher.group(2));

        // Check if there is a specific discount percentage (group 3), otherwise it defaults to 100% (free)
        float discountPercent = 1.0f;
        if (matcher.group(3) != null) {
            discountPercent = Float.parseFloat(matcher.group(3)) / 100.0f;
        }

        // Calculate the cycle size
        int cycleSize = buy + get; // For example, in 1+1 the cycle size is 2
        int fullCycles = totalQuantity / cycleSize; // Number of times the discount cycle applies fully
        int remainder = totalQuantity % cycleSize;  // Remaining items that don't fit into a full cycle

        // Calculate the price per cycle: (items at full price) + (items at discounted price)
        float pricePerCycle = (buy * unitPrice) + (get * unitPrice * (1.0f - discountPercent));

        // Final price: (full cycles * cycle price) + (remainder items at full price)
        return (fullCycles * pricePerCycle) + (remainder * unitPrice);
    }

}
