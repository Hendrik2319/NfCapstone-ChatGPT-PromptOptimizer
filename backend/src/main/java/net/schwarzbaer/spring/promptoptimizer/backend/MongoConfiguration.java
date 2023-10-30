package net.schwarzbaer.spring.promptoptimizer.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

@Configuration
public class MongoConfiguration {

	@Bean
	public MongoCustomConversions customConversions() {
		return new MongoCustomConversions(
				List.of(
						(ZonedDateTimeReadConverterInt ) date -> date.toInstant().atZone(ZoneId.systemDefault()),
						(ZonedDateTimeWriteConverterInt) zonedDateTime -> Date.from(zonedDateTime.toInstant())
				)
		);
	}

	private interface ZonedDateTimeReadConverterInt  extends Converter<Date, ZonedDateTime> {}
	private interface ZonedDateTimeWriteConverterInt extends Converter<ZonedDateTime, Date> {}
}
