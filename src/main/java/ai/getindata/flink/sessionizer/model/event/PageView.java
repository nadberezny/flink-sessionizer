package ai.getindata.flink.sessionizer.model.event;

import ai.getindata.flink.sessionizer.model.MarketingChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageView {

    private MarketingChannel marketingChannel;

    private String landingPage;
}
