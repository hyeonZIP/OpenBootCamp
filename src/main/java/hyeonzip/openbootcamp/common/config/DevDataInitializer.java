package hyeonzip.openbootcamp.common.config;

import hyeonzip.openbootcamp.bootcamp.domain.Bootcamp;
import hyeonzip.openbootcamp.bootcamp.domain.BootcampTrack;
import hyeonzip.openbootcamp.bootcamp.domain.Slug;
import hyeonzip.openbootcamp.bootcamp.repository.BootcampRepository;
import hyeonzip.openbootcamp.common.enums.OperationType;
import hyeonzip.openbootcamp.common.enums.TechStack;
import hyeonzip.openbootcamp.common.enums.TrackType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Profile("dev")
@Component
@RequiredArgsConstructor
public class DevDataInitializer implements ApplicationRunner {

    private final BootcampRepository bootcampRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (bootcampRepository.count() > 0) {
            return;
        }

        Bootcamp codestates = Bootcamp.create("코드스테이츠", Slug.from("codestates"), null,
            "코드스테이츠는 소프트웨어 엔지니어링 부트캠프입니다.", "https://www.codestates.com");

        codestates.addTrack(BootcampTrack.create(TrackType.FRONTEND, OperationType.ONLINE,
            List.of(TechStack.REACT, TechStack.TYPESCRIPT, TechStack.NEXT_JS), 3000000, 5000000, 24,
            true));

        codestates.addTrack(BootcampTrack.create(TrackType.BACKEND, OperationType.ONLINE,
            List.of(TechStack.JAVA, TechStack.SPRING_BOOT, TechStack.MYSQL), 3000000, 5000000, 24,
            false));

        Bootcamp woowacourse = Bootcamp.create("우아한테크코스", Slug.from("woowacourse"), null,
            "우아한형제들이 운영하는 개발자 양성 교육 과정입니다.", "https://woowacourse.github.io");

        woowacourse.addTrack(BootcampTrack.create(TrackType.BACKEND, OperationType.OFFLINE,
            List.of(TechStack.JAVA, TechStack.SPRING_BOOT, TechStack.MYSQL, TechStack.DOCKER), 0, 0,
            52, true));

        woowacourse.addTrack(BootcampTrack.create(TrackType.FRONTEND, OperationType.OFFLINE,
            List.of(TechStack.JAVASCRIPT, TechStack.REACT, TechStack.TYPESCRIPT), 0, 0, 52, true));

        Bootcamp hanghae = Bootcamp.create("항해99", Slug.from("hanghae99"), null,
            "99일간의 집중 개발 부트캠프입니다.", "https://hanghae99.spartacodingclub.kr");

        hanghae.addTrack(BootcampTrack.create(TrackType.FULLSTACK, OperationType.ONLINE,
            List.of(TechStack.REACT, TechStack.NODE_JS, TechStack.MONGODB), 2990000, 2990000, 14,
            true));

        hanghae.addTrack(BootcampTrack.create(TrackType.BACKEND, OperationType.ONLINE,
            List.of(TechStack.JAVA, TechStack.SPRING_BOOT, TechStack.POSTGRESQL, TechStack.REDIS),
            2990000, 2990000, 14, false));

        // 패스트캠퍼스
        Bootcamp fastcampus = Bootcamp.create("패스트캠퍼스", Slug.from("fastcampus"), null,
            "다양한 직군의 실무 중심 교육을 제공하는 부트캠프입니다.", "https://fastcampus.co.kr");

        fastcampus.addTrack(BootcampTrack.create(TrackType.DATA, OperationType.HYBRID,
            List.of(TechStack.PYTHON, TechStack.PANDAS, TechStack.SCIKIT_LEARN, TechStack.PYTORCH),
            2000000, 4000000, 20, true));

        fastcampus.addTrack(BootcampTrack.create(TrackType.DEVOPS, OperationType.ONLINE,
            List.of(TechStack.DOCKER, TechStack.KUBERNETES, TechStack.AWS, TechStack.CI_CD,
                TechStack.LINUX), 2500000, 3500000, 16, true));

        Bootcamp zerobase = Bootcamp.create("제로베이스", Slug.from("zerobase"), null,
            "비전공자도 시작할 수 있는 개발 부트캠프입니다.", "https://zero-base.co.kr");

        zerobase.addTrack(BootcampTrack.create(TrackType.FRONTEND, OperationType.ONLINE,
            List.of(TechStack.JAVASCRIPT, TechStack.REACT, TechStack.TYPESCRIPT), 1490000, 1990000,
            20, true));

        zerobase.addTrack(BootcampTrack.create(TrackType.AI_ML, OperationType.ONLINE,
            List.of(TechStack.PYTHON, TechStack.TENSORFLOW, TechStack.PYTORCH, TechStack.PANDAS),
            1990000, 2490000, 24, false));

        bootcampRepository.saveAll(List.of(codestates, woowacourse, hanghae, fastcampus, zerobase));
    }
}
