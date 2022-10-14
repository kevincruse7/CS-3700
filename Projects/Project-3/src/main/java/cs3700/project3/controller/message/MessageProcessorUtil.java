package cs3700.project3.controller.message;

import cs3700.project3.Config;
import lombok.NonNull;

class MessageProcessorUtil {
    static String getSrcAddressFrom(@NonNull String nextHop) {
        return nextHop.substring(0, nextHop.length() - 1) + Config.SOURCE_ADDRESS_LEAST_SIGNIFICANT_BYTE;
    }
}
